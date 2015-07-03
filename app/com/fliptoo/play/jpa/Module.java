package com.fliptoo.play.jpa;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import scala.collection.Seq;

public class Module extends play.api.inject.Module {

    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(bind(PlayJPA.class).toProvider(PlayJPA.Provider.class).eagerly()
        );
    }
}
