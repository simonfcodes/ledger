package dev.simoncodes.ledger.category;

import dev.simoncodes.ledger.category.dto.CategoryRequest;
import dev.simoncodes.ledger.category.view.CategoryView;
import dev.simoncodes.ledger.user.UserDetailsAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/categories")
@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categorySvc;

    @GetMapping
    public List<CategoryView> getAllSystemAndUserCategories(@AuthenticationPrincipal UserDetailsAdapter principal) {
        return categorySvc.getCategoriesForUser(principal.getUserId())
                .stream()
                .map(CategoryView::fromCategoryWithHiddenStatus)
                .toList();
    }

    @GetMapping("/{id}")
    public CategoryView getCategory(@AuthenticationPrincipal UserDetailsAdapter principal, @PathVariable UUID id) {
        return CategoryView.fromCategoryWithHiddenStatus(categorySvc.getSingleCategory(principal.getUserId(), id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryView createCategory(
            @AuthenticationPrincipal UserDetailsAdapter principal,
            @Valid @RequestBody CategoryRequest req
    ) {
        return CategoryView.fromCategoryWithHiddenStatus(categorySvc.createCategory(principal.getUserId(), req));
    }

    @PutMapping("/{id}")
    public CategoryView updateCategory(@AuthenticationPrincipal UserDetailsAdapter principal,
                                       @PathVariable UUID id,
                                       @Valid @RequestBody CategoryRequest req
    ) {
        return CategoryView.fromCategoryWithHiddenStatus(categorySvc.updateCategory(principal.getUserId(), id, req));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCategory(@AuthenticationPrincipal UserDetailsAdapter principal,
                               @PathVariable UUID id
    ) {
        categorySvc.deleteCategory(principal.getUserId(), id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/hide")
    public void hideCategory(@AuthenticationPrincipal UserDetailsAdapter principal,
                             @PathVariable UUID id
    ) {
        categorySvc.hideSystemCategory(principal.getUserId(), id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/hide")
    public void unhideCategory(@AuthenticationPrincipal UserDetailsAdapter principal,
                               @PathVariable UUID id
    ) {
        categorySvc.unhideSystemCategory(principal.getUserId(), id);
    }

}
