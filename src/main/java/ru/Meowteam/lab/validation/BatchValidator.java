package ru.Meowteam.lab.validation;

import ru.Meowteam.lab.domain.ReagentBatch;
import ru.Meowteam.lab.exception.ValidationException;

public class BatchValidator {
    public void validate(ReagentBatch batch) {
        if (batch.getLabel() == null || batch.getLabel().trim().isEmpty()) {
            throw new ValidationException("label не может быть пустым");
        }
        if (batch.getLabel().length() > 64) {
            throw new ValidationException("label слишком длинный (макс. 64)");
        }
        if (batch.getQuantityCurrent() < 0) {
            throw new ValidationException("количество не может быть отрицательным");
        }
        if (batch.getLocation() == null || batch.getLocation().trim().isEmpty()) {
            throw new ValidationException("location не может быть пустым");
        }
        if (batch.getLocation().length() > 64) {
            throw new ValidationException("location слишком длинный (макс. 64)");
        }
        if (batch.getUnit() == null) {
            throw new ValidationException("единицы измерения не указаны");
        }
    }
}