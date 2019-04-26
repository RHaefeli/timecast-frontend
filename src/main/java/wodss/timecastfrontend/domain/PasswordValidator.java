package wodss.timecastfrontend.domain;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Employee.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
                "required.password", "Field name is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword",
                "required.confirmPassword", "Field name is required.");

        Employee employee = (Employee) target;

        if (employee.getPassword().length() < 12) {
            errors.rejectValue("password", "tooshort.password");
        }
        if(!(employee.getPassword().equals(employee.getConfirmPassword()))){
            errors.rejectValue("password", "notmatch.password");
        }
    }
}
