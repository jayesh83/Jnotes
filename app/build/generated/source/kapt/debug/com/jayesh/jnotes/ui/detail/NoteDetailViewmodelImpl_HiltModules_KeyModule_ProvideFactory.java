// Generated by Dagger (https://dagger.dev).
package com.jayesh.jnotes.ui.detail;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class NoteDetailViewmodelImpl_HiltModules_KeyModule_ProvideFactory implements Factory<String> {
  @Override
  public String get() {
    return provide();
  }

  public static NoteDetailViewmodelImpl_HiltModules_KeyModule_ProvideFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static String provide() {
    return Preconditions.checkNotNullFromProvides(NoteDetailViewmodelImpl_HiltModules.KeyModule.provide());
  }

  private static final class InstanceHolder {
    private static final NoteDetailViewmodelImpl_HiltModules_KeyModule_ProvideFactory INSTANCE = new NoteDetailViewmodelImpl_HiltModules_KeyModule_ProvideFactory();
  }
}
