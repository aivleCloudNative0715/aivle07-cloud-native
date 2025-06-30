package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.beans.BeanUtils; // BeanUtils 사용을 위한 import 추가
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import aivlecloudnative.infra.BookWorkRequestDto;

@RestController
@RequestMapping(value = "/bookWorks")
@Transactional
public class BookWorkController {

    @Autowired
    BookWorkRepository bookWorkRepository;

    @GetMapping("/{id}")
    public Optional<BookWork> getBookWorkById(@PathVariable Long id) {
        return bookWorkRepository.findById(id);
    }

    @PostMapping
    public BookWork createBookWork(@RequestBody BookWorkRequestDto requestDto) {
        // 1. DTO의 속성을 PublicationRequested 이벤트 객체로 복사
        PublicationRequested publicationRequested = new PublicationRequested();
        BeanUtils.copyProperties(requestDto, publicationRequested);

        // 2. BookWork 엔티티의 static 메서드를 호출하여 비즈니스 로직 실행
        // 이 메서드 안에서 BookWork가 저장되고 PublicationInfoCreationRequested 이벤트가 발행될 것입니다.
        // 이 메서드는 이제 저장된 BookWork 인스턴스를 반환하도록 BookWork.java 파일을 수정해야 합니다.
        BookWork createdBookWork = BookWork.requestNewBookPublication(publicationRequested);

        // 3. 비즈니스 로직 처리 후 반환된 BookWork 객체를 클라이언트에 응답
        return createdBookWork;
    }

    // 기타 필요한 CRUD 메서드 (PUT, DELETE 등)를 추가할 수 있습니다.
}