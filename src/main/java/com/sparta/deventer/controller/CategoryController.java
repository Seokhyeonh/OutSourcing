package com.sparta.deventer.controller;

import com.sparta.deventer.dto.CategoryRequestDto;
import com.sparta.deventer.dto.CategoryResponseDto;
import com.sparta.deventer.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 생성
     *
     * @param requestDto 카테고리 이름
     * @return 카테고리 정보
     */
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> createCategory(
            @Valid @RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity.ok().body(categoryService.createCategory(requestDto));
    }

    /**
     * 카테고리 목록 조회
     *
     * @return 카테고리 목록
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategory() {
        return ResponseEntity.ok().body(categoryService.getAllCategory());
    }

    /**
     * 카테고리 이름 변경
     *
     * @param categoryId 변경할 카테고리 고유번호
     * @param requestDto 변경할 카테고리 이름
     * @return 수정된 카테고리 정보
     */
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponseDto> changeCategory(@PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity.ok().body(categoryService.changeCategory(categoryId, requestDto));
    }

    /**
     * 카테고리 삭제
     *
     * @param categoryId 삭제할 카테고리 고유번호
     * @return 삭제 메세지
     */
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok().body(categoryService.deleteCategory(categoryId));
    }
}
