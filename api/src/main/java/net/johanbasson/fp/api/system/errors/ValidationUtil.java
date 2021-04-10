package net.johanbasson.fp.api.system.errors;

import cyclops.control.Maybe;
import cyclops.data.tuple.Tuple2;
import net.johanbasson.fp.api.types.Validatable;

import java.util.List;

public class ValidationUtil {

    public static Maybe<ValidationErrors> validate(List<Tuple2<String, Validatable>> items) {
        ValidationErrors errors = new ValidationErrors();
        items.forEach(tuple -> {
            if (tuple._2() == null) {
                errors.add(tuple._1(), String.format("%s is required", tuple._1()));
            } else {
                tuple._2().validate()
                        .flatMap(fieldError -> {
                            errors.add(fieldError.field(), fieldError.message());
                            return null;
                        });
            }
        });

        if (errors.hasErrors()) {
            return Maybe.just(errors);
        } else {
            return Maybe.nothing();
        }
    }
}
