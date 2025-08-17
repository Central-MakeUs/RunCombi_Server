package com.runcombi.server.domain.version.service;

import com.runcombi.server.domain.version.dto.RequestVersionDto;
import com.runcombi.server.domain.version.entity.OS;
import com.runcombi.server.domain.version.entity.Version;
import com.runcombi.server.domain.version.repository.VersionRepository;
import jakarta.transaction.Transactional;
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

    public HashMap<String, String> getVersion() {
        Optional<Version> optionaliOS = versionRepository.findByOs(OS.iOS);
        Optional<Version> optionalAndroid = versionRepository.findByOs(OS.Android);
        HashMap<String, String> result = new HashMap<>();

        if(optionaliOS.isEmpty()) {
            result.put("iOS", "최소 버전 없음");
        }else {
            result.put("iOS", optionaliOS.get().getVersion());
        }

        if(optionalAndroid.isEmpty()) {
            result.put("Android", "최소 버전 없음");
        }else {
            result.put("Android", optionalAndroid.get().getVersion());
        }

        return result;
    }

    @Transactional
    public void updateVersion(RequestVersionDto requestVersionDto) {
        Optional<Version> optionalVersion = versionRepository.findByOs(requestVersionDto.getOs());
        if(optionalVersion.isEmpty()) {
            // 저장된 버전 정보가 없는 경우
            versionRepository.save(
                    Version.builder()
                            .os(requestVersionDto.getOs())
                            .version(requestVersionDto.getVersion()).build()
            );
        }else {
            // 저장된 버전 정보가 존재하는 경우
            Version version = optionalVersion.get();
            version.updateVersion(requestVersionDto);
            versionRepository.save(version);
        }
    }
}
