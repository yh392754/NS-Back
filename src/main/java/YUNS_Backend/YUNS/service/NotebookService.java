package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.*;
import YUNS_Backend.YUNS.entity.Notebook;
import YUNS_Backend.YUNS.entity.NotebookImage;
import YUNS_Backend.YUNS.repository.NotebookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotebookService {

    final NotebookRepository notebookRepository;
    final S3Service s3Service;

    public Long saveNotebook(NotebookRegistRequestDto notebookRegistRequestDto){
        Notebook notebook = Notebook.createNotebook(notebookRegistRequestDto.getModel(),
                notebookRegistRequestDto.getContent(),
                notebookRegistRequestDto.getManufactureDate(),
                notebookRegistRequestDto.getOs(),
                notebookRegistRequestDto.getSize());

        List<String> imgUrls = null;

        if(notebookRegistRequestDto.getImages() != null && !notebookRegistRequestDto.getImages().isEmpty()){
            imgUrls = notebookRegistRequestDto.getImages().stream()
                    .filter(image -> image.getSize() > 0)
                    .map(image -> s3Service.uploadFile(image))
                    .toList();

            notebook.updateImages(imgUrls, null);
        }

        notebookRepository.save(notebook);

        return notebook.getNotebookId();
    }

    public void updateNotebook(NotebookUpdateRequestDto notebookUpdateRequestDto, Long notebookId){
        Notebook notebook = notebookRepository.findByNotebookId(notebookId).orElseThrow(EntityNotFoundException::new);

        List<String> newImgUrls = null;

        notebook.getImages().forEach(image -> {
            String imageUrl = image.getImageUrl();
            if (!notebookUpdateRequestDto.getImageUrls().contains(imageUrl))
                s3Service.deleteFile(imageUrl);
        });

        if(notebookUpdateRequestDto.getImages() != null && !notebookUpdateRequestDto.getImages().isEmpty()) {
            newImgUrls = notebookUpdateRequestDto.getImages().stream()
                    .filter(image -> image.getSize() > 0)
                    .map(image -> s3Service.uploadFile(image))
                    .toList();
        }

        notebook.updateNotebook(notebookUpdateRequestDto.getModel(),
                notebookUpdateRequestDto.getContent(),
                notebookUpdateRequestDto.getManufactureDate(),
                notebookUpdateRequestDto.getOs(),
                notebookUpdateRequestDto.getSize());

        notebook.updateImages(newImgUrls, notebookUpdateRequestDto.getImageUrls());
    }

    public void deleteNotebook(Long notebookId){
        Notebook notebook = notebookRepository.findByNotebookId(notebookId).orElseThrow(EntityNotFoundException::new);
        notebook.getImages().forEach(image -> s3Service.deleteFile(image.getImageUrl()));
        notebookRepository.delete(notebook);
    }

    @Transactional(readOnly = true)
    public Page<NotebookListDto> getList(NotebookFilterDto notebookFilterDto, Pageable pageable) {
        return notebookRepository.getNotebookListPage(notebookFilterDto, pageable);
    }

    @Transactional(readOnly = true)
    public Set<Integer> getSize(){
        return notebookRepository.findDistinctSize();
    }

    @Transactional(readOnly = true)
    public Set<String> getModel(){
        return notebookRepository.findDistinctModel();
    }

    @Transactional(readOnly = true)
    public NotebookDetailDto getNotebookDetail(Long id){
        Notebook notebook = notebookRepository.findByNotebookId(id)
                .orElseThrow(EntityNotFoundException::new);

        List<String> imageUrls = notebook.getImages().stream()
                .map(NotebookImage::getImageUrl)
                .collect(Collectors.toList());

        NotebookDetailDto notebookDetailDto = NotebookDetailDto.builder()
                .id(notebook.getNotebookId())
                .model(notebook.getModel())
                .content(notebook.getContent())
                .manufactureDate(notebook.getManufactureDate())
                .os(notebook.getOperatingSystem())
                .rentalStatus(notebook.getRentalStatus().toString())
                .imgUrl(imageUrls)
                .size(notebook.getSize())
                .build();

        return notebookDetailDto;
    }

}