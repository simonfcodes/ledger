package dev.simoncodes.ledger.category.view;

import dev.simoncodes.ledger.category.CategoryWithHiddenStatus;

import java.util.UUID;

public record CategoryView(
        UUID id,
        UUID userId,
        String displayName,
        UUID parentId,
        String color,
        String icon,
        String code,
        boolean hidden
) {
    public static CategoryView fromCategoryWithHiddenStatus(CategoryWithHiddenStatus cat) {
        return new CategoryView(
                cat.id(),
                cat.userId(),
                cat.displayName(),
                cat.parentId(),
                cat.color(),
                cat.icon(),
                cat.code(),
                cat.hidden()
        );
    }
}
