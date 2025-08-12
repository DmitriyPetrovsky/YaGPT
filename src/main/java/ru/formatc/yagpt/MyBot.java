package ru.formatc.yagpt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.formatc.yagpt.model.Dialog;
import ru.formatc.yagpt.repository.DialogRepository;
import ru.formatc.yagpt.service.GptService;


import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MyBot extends TelegramLongPollingBot {
    private final GptService gptService;
    private final DialogRepository dialogRepository;

    @Override
    public String getBotUsername() {
        return "YOUR_BOT_NAME";
    }

    @Override
    public String getBotToken() {
        return "YOUR_TELEGRAM_TOKEN";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if ("/new".equals(update.getMessage().getText())) {
                try {
                    setNewContext(update);
                    sendMessage(update.getMessage().getChatId(), "Начинаем новый диалог...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Dialog dialog = createDialog(update);
                    sendMessage(dialog.getUserId(), dialog.getAnswer());
                    dialogRepository.save(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(update.getMessage().getChatId(), "Что-то пошло не так...");
                }
            }
        }
    }

    private Dialog createDialog(Update update) {
        Dialog dialog = new Dialog();
        dialog.setUserId(update.getMessage().getChatId());
        dialog.setUserName(update.getMessage().getFrom().getUserName());
        dialog.setQuestion(update.getMessage().getText());
        dialog.setContextId(setDialogContextId(update));
        dialog.setNewContext(false);

        // Получаем ответ с учетом контекста
        dialog.setAnswer(gptService.generateText(
                dialog.getUserId(),
                dialog.getContextId(),
                dialog.getQuestion()
        ));

        return dialog;
    }


    private long setDialogContextId(Update update) {
        Optional<Dialog> dialogOpt = dialogRepository.findFirstByUserIdOrderByIdDesc(update.getMessage().getChatId());
        if (dialogOpt.isEmpty()) {
            return 1L;
        }
        Dialog dialog = dialogOpt.get();
        if (dialog.isNewContext()) {
            return dialog.getContextId() + 1;
        }
        return dialog.getContextId();
    }

    private void setNewContext(Update update) {
        Optional<Dialog> dialogOpt = dialogRepository.findFirstByUserIdOrderByIdDesc(update.getMessage().getChatId());
        if (dialogOpt.isPresent()) {
            Dialog dialog = dialogOpt.get();
            dialogRepository.setNewContextFlag(dialog.getId());
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
