package org.springframework.rest.documentation.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.springframework.rest.documentation.doclet.Launcher;
import org.springframework.util.Assert;

@Mojo(name = "generate", requiresProject = true, defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class GenerateDocumentationMojo extends AbstractMojo {
	
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;
     
	@Parameter(defaultValue="${repositorySystemSession}", readonly = true, required = true)
    private RepositorySystemSession repositorySession;
	
	@Component
	private RepositorySystem repositorySystem;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generating REST API documentation");
		
		File sourcePath = getSourcePath();		
		List<String> packages = getPackages(sourcePath);
		List<File> classpath = getClasspath();
		File outputDirectory = new File(this.project.getBuild().getOutputDirectory());
		List<File> docletPath = getDocletPath();
		docletPath.addAll(classpath);
		
		try {
			new Launcher(sourcePath, packages, classpath, docletPath, outputDirectory).launch();
		} catch (IOException e) {
			throw new MojoExecutionException("Javadoc processing failed", e);
		} catch (InterruptedException e) {
			throw new MojoExecutionException("Javadoc processing failed", e);
		}
	}

	private List<File> getDocletPath() throws MojoExecutionException {
		List<File> docletPath = new ArrayList<File>();
		
		for (Plugin buildPlugin: this.project.getBuildPlugins()) {
			if ("rest-documentation-maven-plugin".equals(buildPlugin.getArtifactId())) {
				DependencyRequest request = new DependencyRequest();
				Dependency root = new Dependency(new DefaultArtifact(buildPlugin.getGroupId(), buildPlugin.getArtifactId(), "jar", buildPlugin.getVersion()), "compile");
				request.setCollectRequest(new CollectRequest(root, this.project.getRemotePluginRepositories()));
				DependencyResult result;
				try {
					result = this.repositorySystem.resolveDependencies(this.repositorySession, request);
					for (ArtifactResult artifactResult: result.getArtifactResults()) {
						docletPath.add(artifactResult.getArtifact().getFile());
					}
				} catch (DependencyResolutionException e) {
					throw new MojoExecutionException("Resolution failed", e);
				}
			}
		}
		
		return docletPath;
	}

	private List<File> getClasspath() {
		List<Artifact> artifacts = project.getRuntimeArtifacts();
		List<File> classpath = new ArrayList<File>(artifacts.size());
		for (Artifact artifact: artifacts) {
			classpath.add(artifact.getFile());
		}
		classpath.add(new File(this.project.getBuild().getOutputDirectory()));
		return classpath;
	}
	
	private File getSourcePath() {
		List<String> sourceRoots = this.project.getCompileSourceRoots();
		Assert.state(sourceRoots.size() == 1, "A single source root is required");
		return new File(sourceRoots.get(0));
	}
	
	private List<String> getPackages(File root) {
		List<String> packages = new ArrayList<String>();
		File[] files = root.listFiles();
		if (files != null) {
			for (File file: files) {
				if (file.isDirectory()) {
					packages.add(file.getName());
				}
			}
		}
		return packages;
	}
}
