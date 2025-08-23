const nav = new DayPilot.Navigator("nav", {
  locale: "es-es",
  selectMode: "day",
  showMonths: 1,
  skipMonths: 1,
  startDate: DayPilot.Date.today(),
  onTimeRangeSelected: (args) => {
    calendar.startDate = args.start;
    calendar.update();
  },
});

nav.init();
