import play.api.inject.{SimpleModule, _}
import services.SchedulerService

class Module extends SimpleModule(bind[SchedulerService].toSelf.eagerly())
