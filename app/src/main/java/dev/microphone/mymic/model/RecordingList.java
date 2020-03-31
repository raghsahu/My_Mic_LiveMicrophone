package dev.microphone.mymic.model;

public class RecordingList {

    String recordingUri;
    String fileName;

    public RecordingList(String recordingUri, String fileName) {
        this.recordingUri = recordingUri;
        this.fileName = fileName;
    }

    public String getRecordingUri() {
        return recordingUri;
    }

    public void setRecordingUri(String recordingUri) {
        this.recordingUri = recordingUri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
