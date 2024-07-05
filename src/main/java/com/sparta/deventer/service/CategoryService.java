package com.sparta.deventer.service;

import com.sparta.deventer.dto.CategoryRequestDto;
import com.sparta.deventer.dto.CategoryResponseDto;
import com.sparta.deventer.entity.Category;
import com.sparta.deventer.enums.NotFoundEntity;
import com.sparta.deventer.exception.CategoryDuplicateException;
import com.sparta.deventer.exception.EntityNotFoundException;
import com.sparta.deventer.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 생성 로직
     *
     * @param requestDto 카테고리 이름
     * @return 카테고리 정보
     */
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {

        checkDuplicateCategoryTopic(requestDto.getTopic());

        Category category = new Category(requestDto.getTopic());

        categoryRepository.save(category);

        return makeResponseDto(category);
    }

    /**
     * 카테고리 조회 로직
     *
     * @return 카테고리 정보 묶음
     */
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategory() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponseDto(
                        category.getId(),
                        category.getTopic(),
                        category.getCreatedAt(),
                        category.getUpdateAt()))
                .toList();
    }

    /**
     * 카테고리 이름 변경 로직
     *
     * @param categoryId 변경할 카테고리 고유번호
     * @param requestDto 변경할 카테고리 이름
     * @return 변경된 카테고리 정보
     */
    @Transactional
    public CategoryResponseDto changeCategory(Long categoryId, CategoryRequestDto requestDto) {
        Category category = getCategoryById(categoryId);

        checkDuplicateCategoryTopic(requestDto.getTopic());

        category.updateTopic(requestDto.getTopic());

        return makeResponseDto(category);
    }

    /**
     * 카테고리 삭제 로직
     *
     * @param categoryId 삭제할 카테고리 고유번호
     * @return 삭제 완료 메세지
     */
    @Transactional
    public String deleteCategory(Long categoryId) {

        Category category = getCategoryById(categoryId);

        categoryRepository.delete(category);

        return "[" + category.getTopic() + "] 라는 카테고리가 삭제되었습니다.";
    }

    private void checkDuplicateCategoryTopic(String topic) {
        if (categoryRepository.existsByTopic(topic)) {
            throw new CategoryDuplicateException("이미 존재하는 카테고리입니다.");
        }
    }

    /**
     * 카테고리 객체 반환 로직
     *
     * @param categoryId 찾아올 카테고리 고유번호
     * @return 카테고리 객체
     */
    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.CATEGORY_NOT_FOUND));
    }

    /**
     * 카테고리 정보 생성 로직
     *
     * @param category 기반이 될 카테고리 객체
     * @return 정보를 담은 카테고리 객체
     */
    private CategoryResponseDto makeResponseDto(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getTopic(),
                category.getCreatedAt(),
                category.getUpdateAt()
        );
    }
}