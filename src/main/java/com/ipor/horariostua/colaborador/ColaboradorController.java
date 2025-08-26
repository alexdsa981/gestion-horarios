package com.ipor.horariostua.colaborador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app/colaboradores")
public class ColaboradorController {
    @Autowired
    ColaboradorService colaboradorService;


}
