package de.simplyroba.pixoobridge.bridge.draw

import de.simplyroba.pixoobridge.bridge.draw.model.RGB
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Draw")
@RestController
@RequestMapping("/draw")
class DrawController(private val drawService: DrawService) {

  @Operation(description = "Fill complete screen with rgb color")
  @PostMapping("/fill", consumes = [APPLICATION_JSON_VALUE])
  fun fill(@RequestBody rgb: RGB): ResponseEntity<Void> {
    drawService.drawColor(rgb)
    return ok().build()
  }

  @Operation(description = "Upload an image")
  @PostMapping("/upload", consumes = [MULTIPART_FORM_DATA_VALUE])
  fun uploadImage(
    @RequestPart("image", required = true) image: MultipartFile
  ): ResponseEntity<Void> {
    drawService.drawImage(image.resource)
    return ok().build()
  }
}
