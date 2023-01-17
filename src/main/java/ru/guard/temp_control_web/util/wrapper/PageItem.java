package ru.guard.temp_control_web.util.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageItem {
    private int number;
    private boolean current;

}
