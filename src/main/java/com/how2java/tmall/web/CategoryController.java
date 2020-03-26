package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public Page4Navigator<Category> list(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) throws Exception {
        start = start < 0 ? 0 : start;
        Page4Navigator<Category> page = categoryService.list(start, size, 5);
        return page;
    }

    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        categoryService.add(bean);
        saveOrUpdateImageFile(bean, image, request);
        return bean;
    }

    private void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {

        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, bean.getId() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }

    @DeleteMapping("/categories/{id}")
    public Object delete(
            @PathVariable(name = "id")int id,
            HttpServletRequest request
    ) throws Exception {
        categoryService.delete(id);
        String realPath = request.getServletContext().getRealPath("img/category"+id + ".jpg");
        File file = new File(realPath);
        file.delete();
        return null;
    }

    @GetMapping("/categories/{id}")
    public Category get(
            @PathVariable(name = "id")int id) throws Exception {
        return categoryService.get(id);
    }

    @PutMapping("/categories/{id}")
    public Object update(
            Category bean,
            MultipartFile image,
            HttpServletRequest request
    ) throws  Exception {
        String name = request.getParameter("name");
        bean.setName(name);
        categoryService.update(bean);

        if (null != image) {
            saveOrUpdateImageFile(bean,image,request);
        }
        return bean;

    }
}
