package dev.simoncodes.ledger.category;

import dev.simoncodes.ledger.category.dto.CategoryRequest;
import dev.simoncodes.ledger.common.exception.BadRequestException;
import dev.simoncodes.ledger.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepo;

    public List<CategoryWithHiddenStatus> getCategoriesForUser(UUID userId) {
        return categoryRepo.getAllWithHiddenStatusByUserId(userId);
    }

    public CategoryWithHiddenStatus getSingleCategory(UUID userId, UUID categoryId) {
        return categoryRepo.getSingleCategory(userId, categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    public CategoryWithHiddenStatus createCategory(UUID userId, CategoryRequest req) {
        checkParentEligibility(userId, req.parentId());

        Category cat = new Category();
        cat.setUserId(userId);
        cat.setDisplayName(req.displayName());
        cat.setParentId(req.parentId());
        cat.setColor(req.color());
        cat.setIcon(req.icon());
        cat.setCode(null);
        Category saved = categoryRepo.save(cat);
        log.info("Created category with ID: {}", saved.getId());
        return new CategoryWithHiddenStatus(
                saved.getId(),
                saved.getUserId(),
                saved.getDisplayName(),
                saved.getParentId(),
                saved.getColor(),
                saved.getIcon(),
                saved.getCode(),
                false
        );
    }

    public CategoryWithHiddenStatus updateCategory(UUID userId, UUID categoryId, CategoryRequest req) {
        Category cat = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        if (!cat.getUserId().equals(userId)) {
            throw new BadRequestException("User not authorized to update category");
        }
        cat.setDisplayName(req.displayName());
        if (categoryId.equals(req.parentId())) {
            throw new BadRequestException("Category cannot be its own parent");
        }
        checkParentEligibility(userId, req.parentId());
        cat.setParentId(req.parentId());
        cat.setColor(req.color());
        cat.setIcon(req.icon());
        Category saved = categoryRepo.save(cat);
        log.info("Updated category with ID: {}", saved.getId());
        return categoryRepo.getSingleCategory(userId, saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + saved.getId()));
    }

    public void deleteCategory(UUID userId, UUID categoryId) {
        if (!categoryRepo.existsByIdAndOwnedByUserId(categoryId, userId)) {
            throw new BadRequestException("Cannot delete category by ID: " + categoryId + " - either does not exist or not owned by requesting user Id");
        }
        categoryRepo.deleteById(categoryId);
        log.info("Deleted category with ID: {}", categoryId);
    }

    public void hideSystemCategory(UUID userId, UUID categoryId) {
        if (!categoryRepo.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        if (!categoryRepo.isSystemCategory(categoryId)) {
            throw new BadRequestException("Cannot hide a non-system category from view. Category with ID: " + categoryId);
        }
        categoryRepo.hideSystemCategoryAndChildrenForUser(userId, categoryId);
    }

    public void unhideSystemCategory(UUID userId, UUID categoryId) {
        if (!categoryRepo.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        if (!categoryRepo.isSystemCategory(categoryId)) {
            throw new BadRequestException("Cannot unhide a non-system category from view. Category with ID: " + categoryId);
        }
        categoryRepo.unhideSystemCategoryForUser(userId, categoryId);
    }

    private void checkParentEligibility(UUID userId, UUID categoryId) {
        if (categoryId == null) return;
        if (!categoryRepo.existsByIdAndUserId(categoryId, userId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        if (categoryRepo.hasParentCategory(categoryId)) {
            throw new BadRequestException("Category with ID: " + categoryId + " already has parent and so cannot be assigned as a parent to another category");
        }
    }
}
