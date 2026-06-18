package com.example.xml_project.controller;


import org.xml.sax.SAXException;

import com.example.xml_project.model.User;
import com.example.xml_project.service.UserService;
import com.example.xml_project.service.XmlValidatorService;

import jakarta.validation.Valid;  // For input validation 
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;   
import org.springframework.web.bind.annotation.*;  // For REST controller

import java.util.List;

@RestController  // Manages http request and returns JSON or XML
@RequestMapping("api/users") //All routes start with /api/users
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    // GET /api/users : returns all the users
    @GetMapping(produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
    })

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET /api/users/{id} : returns the user with the id
    @GetMapping(value = "/{id}", produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
    })

    public ResponseEntity<User> getUsersById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok) // 200 OK if found
                .orElse(ResponseEntity.notFound().build()); // 404 if not found
    }

    // POST /api/users : creates a new user
    // @Valid : @NotBlank, @Email....
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
    })

    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));   // 201 created
    }
    

    // PUT /api/users/{id} : update the user with this id
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE) // We don't need to produce here since we're just updating

    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User updated) {
        return userService.updateUser(id, updated).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());  // map converts the user object into ResponseEntity.ok(user) object
    }


    // DELETE /api/user/{id} = delete the user with this id
    @DeleteMapping(value = "/{id}") // Deleting doesnt't generate any json file

    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();  // 204 no content, 404 not found
    }






    


    //  Validation XML _____________________(mafhemtch)
    private final XmlValidatorService xmlValidatorService; // ← nouveau

    // New endpoint: POST a user as XML, validated via XSD (default) or DTD.
    // Use ?validation=dtd to validate against the DTD instead of the XSD.
    @PostMapping(
        value = "/xml",
        consumes = MediaType.APPLICATION_XML_VALUE,
        produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<?> createUserFromXml(
            @RequestBody String xmlBody,
            @RequestParam(name = "validation", defaultValue = "xsd") String validation) {
        try {
            // 1. Validate the XML against the chosen schema
            if ("dtd".equalsIgnoreCase(validation)) {
                xmlValidatorService.validateWithDtd(xmlBody);
            } else {
                xmlValidatorService.validate(xmlBody);
            }

            // 2. Convert the XML into a User object
            com.fasterxml.jackson.dataformat.xml.XmlMapper xmlMapper =
                new com.fasterxml.jackson.dataformat.xml.XmlMapper();
            User user = xmlMapper.readValue(xmlBody, User.class);

            // 3. Save and return
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(userService.createUser(user));

        } catch (SAXException e) {
            // XML invalid according to the schema (XSD/DTD)
            return ResponseEntity.badRequest()
                                 .body("XML invalide : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                                 .body("Erreur : " + e.getMessage());
        }
    }
}