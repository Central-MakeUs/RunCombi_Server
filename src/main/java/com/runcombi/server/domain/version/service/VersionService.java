package com.runcombi.server.domain.version.service;

import com.runcombi.server.domain.version.entity.OS;
import com.runcombi.server.domain.version.entity.Version;
import com.runcombi.server.domain.version.repository.VersionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VersionService {
    private final VersionRepository versionRepository;

    /**
     * DB 내 저장된 가장 최신의 최소 버전을 찾아 비교하는 메소드
     * @param os Android, iOS
     * @param userVersion ex) 1.0.2
     * @return 응답값
     */
    public Map<String, String> versionCheck(OS os, String userVersion) {
        Optional<Version> limit = versionRepository.findTopByOsOrderByVersionIdDesc(os);
        Map<String, String> result = new HashMap<>();
        if(limit.isEmpty()) {
            result.put("updateRequire", "N");
            return result;
        } else {
            String limitVersion = limit.get().getVersion();
            int versionCompare = compareVersion(userVersion, limitVersion);

            if(versionCompare < 0) {
                result.put("updateRequire", "Y");
            }else {
                result.put("updateRequire", "N");
            }
            return result;
        }
    }

    /**
     * 버전을 비교합니다.
     * 사용자 버전이 최소보다 낮을 경우 -1 을 반환
     * 높은 경우 0 을 반환
     * 낮은 경우 1 을 반환합니다.
     * @param v1 최소 버전
     * @param v2 사용자 버전
     * @return -1,0,1
     */
    public static int compareVersion(String v1, String v2) {
        int[] v1Arr = parseVersion(v1);
        int[] v2Arr = parseVersion(v2);

        int maxLen = Math.max(v1Arr.length, v2Arr.length);
        for (int i = 0; i < maxLen; i++) {
            int num1 = (i < v1Arr.length) ? v1Arr[i] : 0;
            int num2 = (i < v2Arr.length) ? v2Arr[i] : 0;

            if (num1 < num2) return -1;
            if (num1 > num2) return 1;
        }
        return 0;
    }

    // 버전 정보를 정수형 배열로 분리
    // ex) 1.3.2 -> [1,3,2]

    /**
     * 문자열의 버전 정보를 정수 배열로 분리해줍니다.
     * ex) 1.3.2 -> [1,3,2]
     * @param version 문자열 버전 정보
     * @return 정수형 배열
     */
    public static int[] parseVersion(String version) {
        return Arrays.stream(version.split("\\."))
                .mapToInt(Integer::parseInt)
                .toArray();
    }
}
