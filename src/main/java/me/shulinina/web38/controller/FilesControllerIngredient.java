package me.shulinina.web38.controller;
import me.shulinina.web38.service.IngredientFileService;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
@RestController
@RequestMapping("/ingredientFiles")
public class FilesControllerIngredient {
    private  final IngredientFileService ingredientFileService;
    public FilesControllerIngredient(IngredientFileService ingredientFileService) {
        this.ingredientFileService = ingredientFileService;
    }
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> downloadFile() throws FileNotFoundException {
        File file = ingredientFileService.getDataFileIng();
        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"IngredientsLog.json\"")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadDataFile(@RequestParam MultipartFile file) {
        ingredientFileService.cleanDataFileIng();
        File dataFile = ingredientFileService.getDataFileIng();
        try (FileOutputStream fos = new FileOutputStream(dataFile)) {
            IOUtils.copy(file.getInputStream(), fos);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}