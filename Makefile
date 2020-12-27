ARGS ?:

build-jar:
	./gradlew clean shadowJar

desktop-notifier: ./exec
	./exec $(ARGS)

build-native-exec: build-jar
	${GRAALVM_HOME}/bin/native-image -cp ${HOME}/.gradle/caches/modules-2/files-2.1/info.picocli/picocli/4.5.2/265314f98a6e7beed510f3bd7b5eaf1bfcaf9d50/picocli-4.5.2.jar --static -jar ./build/libs/desktop-notifier-1.0-SNAPSHOT-all.jar desktop-notifier
