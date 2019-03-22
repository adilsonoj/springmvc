package com.aula.springmvc.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aula.springmvc.dao.ProdutoDAO;
import com.aula.springmvc.infra.FileSaver;
import com.aula.springmvc.models.Produto;
import com.aula.springmvc.models.TipoPreco;
import com.aula.springmvc.validation.ProdutoValidation;

@Controller
@RequestMapping("/produtos")
public class ProdutosController {

	@Autowired
	private ProdutoDAO produtoDao;
	
	@Autowired
    private FileSaver fileSaver;
	
	@InitBinder //responsável por vincular o validador com o controller
	public void initBinder(WebDataBinder binder) {
	    binder.addValidators(new ProdutoValidation());
	}
	
	@RequestMapping("form")
	public ModelAndView form(Produto produto) {
		
		ModelAndView modelAndView = new ModelAndView("produtos/form");
		modelAndView.addObject("tipos", TipoPreco.values());
		
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView gravar(MultipartFile sumario, @Valid Produto produto, BindingResult result,  RedirectAttributes redirectAttributes) {
		
		//se houver erros, retorne o método form()
		 if (result.hasErrors()) {
		        return form(produto);
		    }
		 
		String path = fileSaver.write("arquivos-sumario", sumario);
	    produto.setSumarioPath(path);
	        
		produtoDao.gravar(produto);
		
		redirectAttributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso!"); //redireciona com um atributo para view
		
		return new ModelAndView("redirect:produtos"); //redirect evita o problema de F5 enserir mais objetos
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView listar() {
	    List<Produto> produtos = produtoDao.listar();
	    ModelAndView modelAndView = new ModelAndView("produtos/lista");
	    modelAndView.addObject("produtos", produtos);

	    return modelAndView;
	}
	
	@RequestMapping("/detalhe/{id}")
	public ModelAndView detalhe(@PathVariable("id") Integer id){

	    ModelAndView modelAndView = new ModelAndView("produtos/detalhe");
	    Produto produto = produtoDao.find(id);
	    modelAndView.addObject("produto", produto);

	    return modelAndView;
	}
}
