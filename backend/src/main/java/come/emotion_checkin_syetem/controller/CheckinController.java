package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.entity.EmotionCheckin;
import come.emotion_checkin_syetem.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/checkin")
@CrossOrigin(origins = "*")
public class CheckinController {

    @Autowired
    private CheckinService checkinService;

    @PostMapping
    public String addCheckin(@RequestHeader("Authorization") String authHeader,
                             @RequestBody EmotionCheckin checkin) {
        String token = authHeader.replace("Bearer ", "");
        return checkinService.saveCheckin(token, checkin);
    }

    @GetMapping("/history")
    public List<EmotionCheckin> getHistory(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return checkinService.getCheckinHistory(token);
    }
}
