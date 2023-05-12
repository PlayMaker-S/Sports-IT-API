package PlayMakers.SportsIT.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class JoinCountDto {
    int maxPlayerCount;
    int maxViewerCount;
    int playerCount;
    int viewerCount;

}
