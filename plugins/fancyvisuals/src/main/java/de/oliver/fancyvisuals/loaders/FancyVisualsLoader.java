package de.oliver.fancyvisuals.loaders;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class FancyVisualsLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {

        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("fancyinnovations", "default", "https://repo.fancyinnovations.com/releases").build());
//        resolver.addRepository(new RemoteRepository.Builder("mavencentral", "default", "https://repo1.maven.org/maven2/").build());
        resolver.addDependency(new Dependency(new DefaultArtifact("de.oliver.FancyAnalytics:mc-api:0.1.12"), "compile"));
        resolver.addDependency(new Dependency(new DefaultArtifact("de.oliver.FancyAnalytics:logger:0.0.8"), "compile"));

        classpathBuilder.addLibrary(resolver);
    }

}
