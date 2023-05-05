package ch.zhaw.peer2vehicle.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ch.zhaw.peer2vehicle.model.Mieter;
import ch.zhaw.peer2vehicle.model.MieterCreateDTO;
import ch.zhaw.peer2vehicle.model.MailInformation;
import ch.zhaw.peer2vehicle.repository.MieterRepository;
import ch.zhaw.peer2vehicle.service.MailValidatorService;

import org.springframework.security.access.annotation.Secured; //Mit @Secured wird der Request auf die entsprechende Rolle beschränkt
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api")
public class MieterController {
    @Autowired
    MieterRepository mieterRepository;

    @Autowired
    MailValidatorService mailValidatorService;

    @PostMapping("/mieter")
    @Secured("ROLE_admin")
    public ResponseEntity<Mieter> createMieter(
            @RequestBody MieterCreateDTO fDTO) {
        // Validate email using MailValidatorService
        MailInformation mailInformation = mailValidatorService.validateEmail(fDTO.getEmail());

        // Check if the email is valid and not from a temporary domain
        if (mailInformation.isFormat() && !mailInformation.isDisposable() && mailInformation.isDns()) { // Es ist is und nicht getFormat, da es in MailInformation.java booleans sind.
            Mieter fDAO = new Mieter(fDTO.getEmail(), fDTO.getName());
            Mieter f = mieterRepository.save(fDAO);
            return new ResponseEntity<>(f, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/mieter")
    @Secured("ROLE_admin")
    public ResponseEntity<Page<Mieter>> getAllMieter(
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "2") Integer pageSize) {
        Page<Mieter> allMieter = mieterRepository.findAll(PageRequest.of(pageNumber - 1, pageSize));
        return new ResponseEntity<>(allMieter, HttpStatus.OK);
    }

    @GetMapping("/mieter/{id}")
    @Secured("ROLE_admin")
    public ResponseEntity<Mieter> getMieterById(@PathVariable String id) {
        Optional<Mieter> optMieter = mieterRepository.findById(id);
        // Falls die ID existiert, OK und den Mieter zurückgeben
        // Falls die ID nicht existiert, NOT_FOUND zurückgeben
        if (optMieter.isPresent()) {
            return new ResponseEntity<>(optMieter.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/me/mieter")
    public ResponseEntity<Mieter> assignToMe(@AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        Mieter mieter = mieterRepository.findFirstByEmail(userEmail);
        if (mieter != null) {
            return new ResponseEntity<>(mieter, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}