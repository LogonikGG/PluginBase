# PluginBase

PluginBase is a lightweight framework for Minecraft (Spigot/Paper) plugins.

It helps to manage the plugin lifecycle, player lifecycle, and **communication between plugin systems**.

This project was created while developing large plugins where maintenance became difficult.

### Problems Addressed by This Project:

**Core Problems**:
1. Services depend on each other
2. Excessive boilerplate code in `onEnable` / `onDisable`
3. Plugin reload (e.g., Plugman) corrupting player data state

**Additional Problems**:
4. Saving and loading plugin data
5. Creating Interaction Items
6. A central place to store common utility classes

---

## Usage Example

1. Download the project, build it, publish to maven local and add it as a dependency to your project: `implementation("ru.logonik:PluginBase:X.X.X-SNAPSHOT")`.

2. Create your main plugin class, extending `BootstrapPlugin`:

```java
public final class NewMainExamplePlugin extends BootstrapPlugin {

   @Override
   public void onEnable() {
      saveDefaultConfig();
      initTranslation();

      serviceLocator.registerService(Scheduler.class, new BukkitScheduler(this));
      serviceLocator.registerService(IBuildPrivilegeGameHandler.class, new BuildPrivilegeGameHandler());
      serviceLocator.registerService(CommandManager.class); // empty constructor
      serviceLocator.registerService(ExampleService .class);
      // ...
      serviceLocator.registerService(DoorGui.class);

      super.onEnable();
   }
}
```

3. When the plugin starts, any service can obtain any other registered class via the `ServiceLocator`:

```java
public class ExampleService implements PluginStartListener {

    @Override
    public void start(ServicesLocator servicesLocator) throws Exception {
        Plugin plugin = servicesLocator.getServiceOrThrow(Plugin.class);
    }
}
```

---
# Infrastructure feature:

## Services System / ServiceLocator

Each service is registered via `BukkitServiceLocator`
(`serviceLocator.registerService(ExampleService.class, new ExampleService());`)
and, depending on the interfaces it implements, is processed as follows:

1. `Listener` — Bukkit event listeners will be automatically registered through the Bukkit API.
2. `PluginStartListener` — Called when the plugin starts.
3. `PluginDisableListener` — Called when the plugin stops.
4. `PlayerAvailableListener(Async/Sync)` — Called when a player joins the server or becomes available*.
   (*If the plugin is reloaded, this will be called for every player already on the server).
5. `PlayerQuitListener(Async/Sync)` — Called when a player leaves the server.
---
**check a [wiki](https://github.com/LogonikGG/PluginBase/wiki) for others features**

---

## License
MIT
