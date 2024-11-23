package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.NotebookDetailDto;
import YUNS_Backend.YUNS.dto.NotebookRegistRequestDto;
import YUNS_Backend.YUNS.dto.NotebookFilterDto;
import YUNS_Backend.YUNS.dto.NotebookListDto;
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
        List<String> imgUrls = null;

        if(notebookRegistRequestDto.getImages() != null && !notebookRegistRequestDto.getImages().isEmpty()){
            imgUrls = notebookRegistRequestDto.getImages().stream()
                    .filter(image -> image.getSize() > 0)
                    .map(image -> s3Service.uploadFile(image))
                    .toList();
        }

        Notebook notebook = Notebook.createNotebook(notebookRegistRequestDto.getModel(),
                notebookRegistRequestDto.getManufactureDate(),
                notebookRegistRequestDto.getOs(),
                notebookRegistRequestDto.getSize());
        notebook.updateImages(imgUrls);

        notebookRepository.save(notebook);

        return notebook.getNotebookId();
    }

    public void updateNotebook(NotebookRegistRequestDto notebookRegistRequestDto, Long notebookId){
        Notebook notebook = notebookRepository.findByNotebookId(notebookId).orElseThrow(EntityNotFoundException::new);

        List<String> imgUrls = null;

        notebook.getImages().forEach(image -> s3Service.deleteFile(image.getImageUrl()));

        if(notebookRegistRequestDto.getImages() != null && !notebookRegistRequestDto.getImages().isEmpty()) {
            imgUrls = notebookRegistRequestDto.getImages().stream()
                    .filter(image -> image.getSize() > 0)
                    .map(image -> s3Service.uploadFile(image))
                    .toList();
        }

        notebook.updateNotebook(notebookRegistRequestDto.getModel(),
                notebookRegistRequestDto.getManufactureDate(),
                notebookRegistRequestDto.getOs(),
                notebookRegistRequestDto.getSize());
        notebook.updateImages(imgUrls);
    }

    public void deleteNotebook(Long notebookId){
        Notebook notebook = notebookRepository.findByNotebookId(notebookId).orElseThrow(EntityNotFoundException::new);
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
                .manufactureDate(notebook.getManufactureDate())
                .os(notebook.getOperatingSystem())
                .rentalStatus(notebook.getRentalStatus().toString())
                .imgUrl(imageUrls)
                .size(notebook.getSize())
                .build();

        return notebookDetailDto;
    }

}