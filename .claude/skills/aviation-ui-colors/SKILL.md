---
name: aviation-ui-colors
description: Aviation (EFIS / ECAM / EICAS) color coding for any UI shown to pilots, officers or technicians, such as dashboards, synoptics, alerts and gauges. Use when choosing colors, styling states, alerts or values in such a UI.
---

# Aviation color coding

Based on cockpit EFIS standards (PFD, ND, ECAM/EICAS): color carries meaning to reduce cognitive load, and is never decorative. Screens use additive color synthesis, so colors are pure and luminous.

| Color | Meaning | Usage | Hex |
| :--- | :--- | :--- | :--- |
| Red | Warning / emergency | Condition requiring immediate action: envelope limits, major failures | `#FF0000` |
| Amber | Caution / abnormal | Anomaly to be aware of, no immediate action; approaching limits | `#FFBF00` |
| Green | Engaged / normal | Normal operation, engaged mode, safe dynamic data | `#00FF00` |
| White | Armed / current info | Mode waiting to engage, present status, base text, scales | `#FFFFFF` |
| Magenta | Target / active | Commanded target values, active flight-plan route | `#FF00FF` |
| Cyan | Advisory | Non-critical guidance, sky on the artificial horizon | `#00FFFF` |
| Brown | Earth | Ground on the artificial horizon | `#8B4513` |
| Black | Background | Default screen background | `#000000` |

## Rules

1. **Active (green) vs armed (white).** A mode requested for later is white; once it engages, it turns green.
2. **Magenta is the operator's request.** If the current speed is 250 kt and the selected target is 280 kt, the 280 value or its cursor is magenta.
3. **No decorative colors.** Never make a button red to mean "Submit"; never use red for a minor warning: use amber.
4. **Dark by default.** Black or very dark grey background; colors light up only to convey information (dark cockpit: when all is well, little is lit).

## Checklist before handing back

- Every colored element maps to one row of the table, by meaning, not by look.
- Red appears only where immediate action is required.
- Target or commanded values are magenta; measured values are white or green.
- The background is black or near black.
