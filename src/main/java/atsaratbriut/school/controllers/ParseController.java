package atsaratbriut.school.controllers;

import atsaratbriut.school.service.parser.ParsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/parse")
public class ParseController {

    @Autowired
    ParsService parsService;
    @GetMapping
    public String parse(){
      return parsService.sendFormToSchool();
    }
}
