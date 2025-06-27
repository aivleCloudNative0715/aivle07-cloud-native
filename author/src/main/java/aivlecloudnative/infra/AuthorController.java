package aivlecloudnative.infra;
import aivlecloudnative.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/authors")
@Transactional
public class AuthorController {
    @Autowired
    AuthorRepository authorRepository;

    @RequestMapping(value = "/authors/applyauthor",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public Author applyAuthor(HttpServletRequest request, HttpServletResponse response, 
        ) throws Exception {
            System.out.println("##### /author/applyAuthor  called #####");
            Author author = new Author();
            author.applyAuthor();
            authorRepository.save(author);
            return author;
    }
    @RequestMapping(value = "/authors/judgeauthor",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public Author judgeAuthor(HttpServletRequest request, HttpServletResponse response, 
        ) throws Exception {
            System.out.println("##### /author/judgeAuthor  called #####");
            Author author = new Author();
            author.judgeAuthor();
            authorRepository.save(author);
            return author;
    }
}
//>>> Clean Arch / Inbound Adaptor
