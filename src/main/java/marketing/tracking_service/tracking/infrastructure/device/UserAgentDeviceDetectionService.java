package marketing.tracking_service.tracking.infrastructure.device;

import marketing.tracking_service.tracking.application.command.startsession.DeviceDetectionService;
import marketing.tracking_service.tracking.domain.model.session.DeviceInfo;
import marketing.tracking_service.tracking.domain.model.session.DeviceType;
import marketing.tracking_service.tracking.domain.model.session.ScreenResolution;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserAgentDeviceDetectionService implements DeviceDetectionService {
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
            "(?i)(mobile|android|iphone|ipod|blackberry|windows phone)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern TABLET_PATTERN = Pattern.compile(
            "(?i)(ipad|tablet|kindle|playbook|silk)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern BOT_PATTERN = Pattern.compile(
            "(?i)(bot|crawler|spider|scraper|slurp|baidu|yandex)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern CHROME_PATTERN = Pattern.compile(
            "Chrome/([0-9.]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern FIREFOX_PATTERN = Pattern.compile(
            "Firefox/([0-9.]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SAFARI_PATTERN = Pattern.compile(
            "Version/([0-9.]+).*Safari",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern EDGE_PATTERN = Pattern.compile(
            "Edg/([0-9.]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern WINDOWS_PATTERN = Pattern.compile(
            "Windows NT ([0-9.]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern MAC_PATTERN = Pattern.compile(
            "Mac OS X ([0-9_]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ANDROID_PATTERN = Pattern.compile(
            "Android ([0-9.]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern IOS_PATTERN = Pattern.compile(
            "OS ([0-9_]+) like Mac OS X",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public DeviceInfo detect(String userAgent, Integer screenWidth, Integer screenHeight, String language, String timezone) {
        if (userAgent == null || userAgent.isBlank()) {
            return DeviceInfo.unknown();
        }

        DeviceType deviceType = detectDeviceType(userAgent);
        BrowserInfo browserInfo = detectBrowser(userAgent);
        OsInfo osInfo = detectOs(userAgent);
        ScreenResolution screenResolution = screenWidth != null && screenHeight != null ? ScreenResolution.of(screenWidth, screenHeight) : null;
        return DeviceInfo.builder()
                .deviceType(deviceType)
                .browser(browserInfo.name())
                .browserVersion(browserInfo.version())
                .os(osInfo.name())
                .osVersion(osInfo.version())
                .screenResolution(screenResolution)
                .language(language)
                .timezone(timezone)
                .build();
    }

    private DeviceType detectDeviceType(String userAgent) {
        if (BOT_PATTERN.matcher(userAgent).find()) {
            return DeviceType.BOT;
        }
        if (TABLET_PATTERN.matcher(userAgent).find()) {
            return DeviceType.TABLET;
        }
        if (MOBILE_PATTERN.matcher(userAgent).find()) {
            return DeviceType.MOBILE;
        }
        return DeviceType.DESKTOP;
    }

    private BrowserInfo detectBrowser(String userAgent) {
        var edgeMatcher = EDGE_PATTERN.matcher(userAgent);
        if (edgeMatcher.find()) {
            return new BrowserInfo("Edge", edgeMatcher.group(1));
        }

        var chromeMatcher = CHROME_PATTERN.matcher(userAgent);
        if (chromeMatcher.find()) {
            return new BrowserInfo("Chrome", chromeMatcher.group(1));
        }

        var firefoxMatcher = FIREFOX_PATTERN.matcher(userAgent);
        if (firefoxMatcher.find()) {
            return new BrowserInfo("Firefox", firefoxMatcher.group(1));
        }

        var safariMatcher = SAFARI_PATTERN.matcher(userAgent);
        if (safariMatcher.find()) {
            return new BrowserInfo("Safari", safariMatcher.group(1));
        }

        return new BrowserInfo("Unknown", null);
    }

    private OsInfo detectOs(String userAgent) {
        var windowsMatcher = WINDOWS_PATTERN.matcher(userAgent);
        if (windowsMatcher.find()) {
            return new OsInfo("Windows", mapWindowsVersion(windowsMatcher.group(1)));
        }

        var macMatcher = MAC_PATTERN.matcher(userAgent);
        if (macMatcher.find()) {
            return new OsInfo("macOS", macMatcher.group(1).replace("_", "."));
        }

        var androidMatcher = ANDROID_PATTERN.matcher(userAgent);
        if (androidMatcher.find()) {
            return new OsInfo("Android", androidMatcher.group(1));
        }

        var iosMatcher = IOS_PATTERN.matcher(userAgent);
        if (iosMatcher.find()) {
            return new OsInfo("iOS", iosMatcher.group(1).replace("_", "."));
        }

        if (userAgent.toLowerCase().contains("linux")) {
            return new OsInfo("Linux", null);
        }

        return new OsInfo("Unknown", null);
    }

    private String mapWindowsVersion(String ntVersion) {
        return switch (ntVersion) {
            case "10.0" -> "10/11";
            case "6.3" -> "8.1";
            case "6.2" -> "8";
            case "6.1" -> "7";
            case "6.0" -> "Vista";
            case "5.1", "5.2" -> "XP";
            default -> ntVersion;
        };
    }
}

