# CurseForge listing text

The text used on the Jogging CurseForge project page. Copy each block into the matching field.

## Summary

```
Jog at sprint speed for as long as you like without using stamina. The catch: you can't fight while jogging, and you're winded when you stop.
```

## Description

````markdown
Jogging is for covering long distances. Type `/jog start` and you run at sprint speed without holding the sprint key, and your stamina bar never goes down.

The trade-off is that you can't do anything else while jogging. When you stop, you're **winded** until your stamina comes back.

## How it works

- **Jogging:** running forward is as fast as sprinting, and stamina stays full. A green JOGGING label shows in the corner.
- **Winded:** when you stop, your stamina drops to empty and refills over about 10 seconds. You can't sprint until it's full. An orange WINDED label shows in the corner.
- **Locked actions:** while jogging or winded, you can't attack, mine, block, place blocks, eat or drink, use weapon abilities, or open doors and chests. Picking up items still works.

So if you jog into a fight, you have to stop first and then get through about 10 seconds with no stamina and no way to fight back.

## Commands

| Command | What it does |
|---|---|
| `/jog start` | Start jogging |
| `/jog stop` | Stop jogging and become winded |

Any player can use these. No admin permissions needed.

## Installing

Needs Hytale **0.7.7** or newer.

1. Put the jar in your `UserData/Mods` folder.
2. Turn the mod on in your world's settings.

## Source and issues

Source code and bug reports: https://github.com/tbfox/jogging
````
