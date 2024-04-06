package ru.yandex.practicum.filmorate.service.log;

import java.util.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.OperationLog;
import ru.yandex.practicum.filmorate.storage.user.LogStorage;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final LogStorage logStorage;

    @Around(value = "@annotation(ru.yandex.practicum.filmorate.model.OperationLog)")
    public void aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

        var signature = (MethodSignature) joinPoint.getSignature();
        var method = signature.getMethod();
        var operation = method.getAnnotation(OperationLog.class);
        List<Object> idList = Arrays.asList(joinPoint.getArgs());
        var o = operation.operation();
        var e = operation.eventType();

        logStorage.saveLog((Integer) idList.get(0), (Integer) idList.get(1), e, o);
    }
}
