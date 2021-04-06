package com.oop.inteliframework.config.file.prerequisite;

import lombok.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Paths {
  public static final char JAR_SEPARATOR = File.separatorChar;

  public static void copyResourcesFromJar(
      Predicate<String> resourceNameFilter,
      @NonNull Class<?> source,
      @NonNull File destination,
      CopyOption copyOption) {
    File fullPath = getFullPath(source);
    if (fullPath == null) throw new IllegalStateException("Jar not found");

    copyResourcesFromJar(resourceNameFilter, fullPath, destination, copyOption);
  }

  public static void copyResourcesFromJar(
      Predicate<String> resourceNameFilter,
      @NonNull File source,
      @NonNull File destination,
      CopyOption copyOption) {
    if (!destination.exists()) {
      destination.mkdirs();
    }

    if (resourceNameFilter == null) resourceNameFilter = $ -> true;

    byte[] buffer = new byte[1024];

    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        Path entryPath = destination.toPath().toAbsolutePath().resolve(entry.getName());
        File file = new File(entryPath.toAbsolutePath().toString());

        if (entry.getName().charAt(entry.getName().length() - 1) == JAR_SEPARATOR) {
          continue;
        }

        if (!resourceNameFilter.test(entry.getName())) continue;

        if (!Files.exists(entryPath.getParent())) {
            Files.createDirectories(entryPath.getParent());
        }

        if (copyOption == CopyOption.COPY_IF_NOT_EXIST && file.exists()) continue;

        FileOutputStream fos = new FileOutputStream(file);

        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
        fos.close();
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public static File getFullPath(Class<?> source) {
    try {
      String path = source.getProtectionDomain().getCodeSource().getLocation().getPath();
      String decodedPath = URLDecoder.decode(path, "UTF-8").replace(" ", "%20");

      if (!decodedPath.startsWith("file")) {
        decodedPath = "file://" + decodedPath;
      }
      return new File(new URI(decodedPath));

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return null;
  }

  public enum CopyOption {
    COPY_IF_NOT_EXIST,
    REPLACE_IF_EXIST
  }
}
