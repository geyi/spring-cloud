package com.airing.spring.cloud.base.utils.delay.queue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.LinkedBlockingQueue;

@Getter
@Setter
@NoArgsConstructor
public class Slot {
    private int number;
    private LinkedBlockingQueue<DelayTask> taskSet = new LinkedBlockingQueue<>();

    public Slot(int number) {
        this.number = number;
    }
}
