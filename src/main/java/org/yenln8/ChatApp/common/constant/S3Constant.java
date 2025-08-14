package org.yenln8.ChatApp.common.constant;

import java.util.List;

public final class S3Constant {
    public static final String AVATAR_PRIVATE_BUCKET = "avatar-private-bucket";
    public static final String AVATAR_GROUP_BUCKET = "avatar-group-bucket";
    public static final String CHAT_MEDIA_BUCKET = "chat-media-bucket";
    public static final String COUNTRY_BUCKET = "country-bucket";
    public static final long PRESIGN_URL_UPLOAD_MEDIA_EXPIRE_TIME = 24 * 60 * 60 * 1000;
    public static final long PRESIGN_URL_DOWNLOAD_MEDIA_EXPIRE_TIME = 24 * 60 * 60 * 1000;
    public static final long MAX_LIMIT_RESOURCE = 1 * 1024 * 1024 * 1024;      // 1GB
    public static final long MAX_FILE_SIZE_AVATAR = 100 * 1024 * 1024;      // 100MB

    public static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            // Images - Hình ảnh
            "image/jpeg",
             "image/jpg",
            "image/png",
            "image/gif",
            "image/webp",

            // Videos - Video
            "video/mp4",
            "video/quicktime",
            "video/x-msvideo",
            "video/webm",

            // Audio - Âm thanh",  // ✅ ADD - cho .mov files
            "audio/mpeg",
            "audio/wav",
            "audio/ogg",
            "audio/mp4",
            "audio/aac",
             "audio/mp3",

            // Documents - Tài liệu (KEEP as is - OK)
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .pptx
            "text/plain",
            "text/csv",

            // Archives - File nén (KEEP as is - OK)
            "application/zip",
            "application/x-rar-compressed",
            "application/x-7z-compressed",
            "application/x-tar",
            "application/gzip",

            // Other common types
            "application/json",
            "application/xml",
            "text/xml"
    );

    public static final List<String> ALLOWED_EXTENSIONS = List.of(
            // Images
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp",

            // Videos
            ".mp4", ".mov", ".avi", ".webm",

            // Audio
            ".mp3", ".wav", ".ogg", ".m4a", ".aac",

            // Documents
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt", ".csv",

            // Archives
            ".zip", ".rar", ".7z", ".tar", ".gz"
    );
}