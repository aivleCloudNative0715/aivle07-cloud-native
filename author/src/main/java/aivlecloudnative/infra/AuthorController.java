package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

//<<< Clean Arch / Inbound Adaptor

@RestController
@Transactional
public class AuthorController {
    @Autowired
    AuthorRepository authorRepository;

    @RequestMapping(value = "/authors/applyauthor",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public Author applyAuthor(HttpServletRequest request, HttpServletResponse response
        ) throws Exception {
            System.out.println("##### /author/applyAuthor  called #####");
            Author author = new Author();
            // author.applyAuthor(); // <-- 삭제
            authorRepository.save(author);
            return author;
    }

    @RequestMapping(value = "/authors/judgeauthor",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public Author judgeAuthor(HttpServletRequest request, HttpServletResponse response
        ) throws Exception {
            System.out.println("##### /author/judgeAuthor  called #####");
            Author author = new Author();
            // author.judgeAuthor(); // <-- 삭제
            authorRepository.save(author);
            return author;
    }
}
//>>> Clean Arch / Inbound Adaptor
