/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */
@RestController
@RequestMapping(value = "/blueprints")
public class BlueprintAPIController {

    @Autowired
    BlueprintsServices bpServices;

    /**
     *
     * @return @throws BlueprintNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllBlueprints() throws BlueprintNotFoundException {

        try {
            Map<String, Blueprint> blueprints = new HashMap();

            List<Blueprint> blueprintsArray = new ArrayList<>();
            blueprintsArray.addAll(bpServices.getAllBlueprints());

            for (Blueprint x : blueprintsArray) {
                blueprints.put(x.getName(), x);
            }

            String codeToJson = new Gson().toJson(blueprints);

            return new ResponseEntity<>(codeToJson, HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException ex) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("No se han podido obtener los planos", HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @param authorName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, path = "{authorName}")
    public ResponseEntity<?> getBlueprintsByAuthor(@PathVariable("authorName") String authorName) {
        try {
            Map<String, Blueprint> blueprints = new HashMap();

            List<Blueprint> blueprintsArray = new ArrayList<>();
            blueprintsArray.addAll(bpServices.getBlueprintsByAuthor(authorName));

            for (Blueprint x : blueprintsArray) {
                blueprints.put(x.getName(), x);
            }

            String codeToJson = new Gson().toJson(blueprints);

            return new ResponseEntity<>(codeToJson, HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException ex) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("No se han podido obtener los planos", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "{authorName}/{bpName}")
    public ResponseEntity<?> getBlueprintsByAuthorNameAndBpName(@PathVariable("authorName") String authorName,
            @PathVariable("bpName") String bpName) {
        try {
            Map<String, Blueprint> blueprints = new HashMap();

            blueprints.put(bpServices.getBlueprint(authorName, bpName).getName(),
                    bpServices.getBlueprint(authorName, bpName));

            String codeToJson = new Gson().toJson(blueprints);

            return new ResponseEntity<>(codeToJson, HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException ex) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("No se ha podido obtener el plano", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addNewBlueprint(@RequestBody String blueprint) throws BlueprintPersistenceException {
        //Code Json example {"bp05":{"author":"Carlos","points":[{"x":9,"y":9},{"x":10,"y":10}],"name":"bp05"}}
        try {

            //Pasar el String JSON a un Map
            Type listType = new TypeToken<Map<String, Blueprint>>() {
            }.getType();
            Map<String, Blueprint> result = new Gson().fromJson(blueprint, listType);

            //Obtener las llaves del Map
            Object[] nameKeys = result.keySet().toArray();

            //Añadir el producto a la orden obteniendo los objetos en el Map
            bpServices.addNewBlueprint(result.get(nameKeys[0]));

            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (BlueprintPersistenceException ex) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("No se ha podido añadir el blueprint", HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path = "{authorName}/{bpName}")
    public ResponseEntity<?> updateABlueprint(@PathVariable("authorName") String authorName,
            @PathVariable("bpName") String bpName, @RequestBody String points) throws BlueprintNotFoundException {
        //Code Json example {"1":[{"x":9,"y":9},{"x":10,"y":10}]}
        try {
            Blueprint bpToUpdate = bpServices.getBlueprint(authorName, bpName);

            //Pasar el String JSON a un Map
            Type listType = new TypeToken<Map<String, Point[]>>() {
            }.getType();
            Map<String, Point[]> result = new Gson().fromJson(points, listType);

            //Obtener las llaves del Map
            Object[] nameKeys = result.keySet().toArray();

            bpToUpdate.setPoints(Arrays.asList(result.get(nameKeys[0])));

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (BlueprintNotFoundException ex) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("No se ha podido actualizar el blueprint", HttpStatus.FORBIDDEN);
        }
    }

}
