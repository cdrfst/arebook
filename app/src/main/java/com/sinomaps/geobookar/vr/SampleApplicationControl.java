package com.sinomaps.geobookar.vr;

import com.vuforia.State;

/* renamed from: com.sinomaps.geobookar.vr.SampleApplicationControl */
public interface SampleApplicationControl {
    boolean doDeinitTrackers();

    boolean doInitTrackers();

    boolean doLoadTrackersData();

    boolean doStartTrackers();

    boolean doStopTrackers();

    boolean doUnloadTrackersData();

    void onInitARDone(SampleApplicationException sampleApplicationException);

    void onVuforiaUpdate(State state);
}
