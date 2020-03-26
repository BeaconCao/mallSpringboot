package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator<Property> list(
            @PathVariable(name = "cid")int cid,
            @RequestParam(name = "start" ,defaultValue = "0") int start,
            @RequestParam(name="size",defaultValue = "5") int size
    ) throws Exception {
        start = start>0?start:0;
        Page4Navigator<Property> page = propertyService.list(cid, start, size, 5);
        return page;
    }

    @GetMapping("/properties/{id}")
    public Property get(
            @PathVariable(name = "id")int id
    ) throws Exception{

        Property bean = propertyService.get(id);
        return bean;
    }



    @PostMapping("/properties")
    public Object add(@RequestBody Property bean) throws Exception{

        propertyService.add(bean);
        return bean;
    }

    @DeleteMapping("/properties/{id}")
    public String delete(
            @PathVariable(name = "id") int id
    ) throws Exception{
        propertyService.delete(id);
        return null;
    }

    @PutMapping("/properties")
    public Object update(
            @RequestBody Property bean
    ) throws Exception {
        propertyService.update(bean);
        return bean;

    }

}
