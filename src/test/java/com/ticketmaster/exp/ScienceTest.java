package com.ticketmaster.exp;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ScienceTest {

    Supplier callTracker  = mock(Supplier.class);

    @Before
    public void setUp() throws Exception {
        assertEquals(0, Science.science().getExperimentCount());
    }
    @After
    public void tearDown() throws Exception {
        Science.science().getClearExperiments();
        assertEquals(0, Science.science().getExperimentCount());
    }

    @Test
    public void testDoExperimentDoesNotRebuildExperiment() throws Exception {
        Experiment<String, String> exp = buildExperiment(callTracker);

        // GIVEN
        Science.science().experiment(
                "my-experiment",
                () -> {
                    callTracker.get();
                    return Experiment
                            .simple("my-experiment")
                            .control(() -> "foo")
                            .candidate(() -> "candidate")
                            .build();
                }

        ).call();

        // WHEN
        String str = Science.science().doExperiment("my-experiment");


        // THEN
        assertEquals("foo", str);
        verify(callTracker, times(1)).get();

    }

    public Experiment<String, String> buildExperiment(Supplier callTracker) throws Exception{
        return Science.science().experiment(
                "my-experiment",
                () -> {
                    callTracker.get();
                    return Experiment
                            .<String>simple("my-experiment")
                            .control(() -> "foo")
                            .candidate(() -> "candidate")
                            .build();
                }

        );
    }

    @Test
    public void testRepeatedExperimentCallDoesNotRebuildExperiment() throws Exception {
        // GIVEN
        assertEquals("foo", buildExperiment(callTracker).call());

        // WHEN
        assertEquals("foo", buildExperiment(callTracker).call());

        // THEN
        verify(callTracker, times(1)).get();
    }

    @Test
    public void testGetExperimentShouldNotReturnExperimentWhenNotBuilt() throws Exception {
        // GIVEN
        // no build experiment

        // WHEN
        Optional<Experiment<String, String>> optEx = Science.science().getExperiment("my-experiment");

        // THEN
        assertEquals(false, optEx.isPresent());

    }
    @Test
    public void testGetExperimentShouldReturnExperimentWhenBuilt() throws Exception {
        // GIVEN
        buildExperiment(callTracker);

        // WHEN
        Optional<Experiment<String, String>> optEx = Science.science().getExperiment("my-experiment");

        // THEN
        assertEquals(true, optEx.isPresent());

    }

    @Test
    public void testExperimentsShouldContainExperimentWhenExperimentsBuilt() throws Exception {
        // GIVEN
        assertEquals(true, Science.science().experiments().isEmpty());
        buildExperiment(callTracker);

        // WHEN
        Map<String, Experiment> experimentMap = Science.science().experiments();

        // THEN
        assertEquals(1, experimentMap.size());
        assertNotNull(experimentMap.get("my-experiment"));
    }

    @Ignore
    @Test
    public void testDoExperimentWithNullReturnShouldReturnNull() throws Exception {

        // EXPECT

        // GIVEN
        Science.science().experiment(
                "my-experiment",
                () -> {
                    callTracker.get();
                    return Experiment
                            .<String>simple("my-experiment")
                            .control(() -> (String) null)
                            .candidate(() -> "candidate")
                            .build();
                }

        );

        // WHEN
        String s = Science.science().doExperiment("my-experiment");

        // THEN
        assertNull(s);
    }
}