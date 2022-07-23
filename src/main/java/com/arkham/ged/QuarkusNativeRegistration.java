package com.arkham.ged;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = { org.jboss.logmanager.Level.class })
public class QuarkusNativeRegistration {
    // Nothing to do
}
