package ru.formatc.yagpt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.formatc.yagpt.model.Dialog;
import ru.formatc.yagpt.repository.DialogRepository;
import yandexgpt.client.YandexGptChatClient;

import java.util.List;

@Service
public class GptService {
    private final YandexGptChatClient chatClient;
    private final DialogRepository dialogRepository;

    @Autowired
    public GptService(YandexGptChatClient chatClient, DialogRepository dialogRepository) {
        this.chatClient = chatClient;
        this.dialogRepository = dialogRepository;
    }

    public String generateText(long userId, long contextId, String prompt) {
        // Получаем историю диалога для текущего контекста
        List<Dialog> dialogHistory = dialogRepository.findByUserIdAndContextIdOrderByIdAsc(userId, contextId);

        // Формируем промпт с учетом истории диалога
        StringBuilder messages = new StringBuilder();
        for (Dialog dialog : dialogHistory) {
            messages.append("Пользователь: ").append(dialog.getQuestion()).append("\n");
            messages.append("Бот: ").append(dialog.getAnswer()).append("\n");
        }
        messages.append("Пользователь: ").append(prompt);

        // Отправляем запрос с учетом контекста
        return chatClient.generateByPrompt(messages.toString());
    }
}
