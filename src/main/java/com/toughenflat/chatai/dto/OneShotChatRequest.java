package com.toughenflat.chatai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneShotChatRequest {
    @Length(max = 2500, message = "消息不能超过2500字!")
    private String message;

    private String userId;

    private Integer sessionType = 0;
}
