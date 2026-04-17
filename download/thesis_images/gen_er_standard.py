#!/usr/bin/env python3
"""
E-R Diagram — Standard ER Notation (v3, final)
  矩形 = 实体
  椭圆 = 属性 (PK下划线)
  菱形 = 关系
  A4 width at 300 DPI — readable without scaling, zero overlaps
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.font_manager import fontManager

fontManager.addfont('/usr/share/fonts/truetype/chinese/SimHei.ttf')
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

# ── Figure: A4 width, matched aspect ──────────────────────────────────────
#  Data range: 10.2 wide × 13.0 tall  →  ratio 0.785
#  Figure:      8.27 × 10.54           →  ratio 0.785  (exact match)
FIG_W, FIG_H = 8.27, 10.54
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H), dpi=300)
ax.set_xlim(-1.4, 8.8)
ax.set_ylim(-0.5, 12.5)
ax.set_aspect('equal')
ax.axis('off')
fig.patch.set_facecolor('white')

# ── Sizes ──────────────────────────────────────────────────────────────────
ENT_W, ENT_H   = 1.35, 0.40
ATTR_EH        = 0.30
DIAM_HW, DIAM_HH = 0.50, 0.28   # standard diamond
DIAM_S_HW, DIAM_S_HH = 0.38, 0.20  # small diamond (tight gaps)

FONT_ENT   = 8.5
FONT_ATTR  = 7.0
FONT_REL   = 7.0
FONT_CARD  = 6.0
FONT_TITLE = 12


# ═══════════════════════════════════════════════════════════════════════════
#  Drawing helpers
# ═══════════════════════════════════════════════════════════════════════════

def draw_entity(cx, cy, name):
    rect = mpatches.FancyBboxPatch(
        (cx - ENT_W/2, cy - ENT_H/2), ENT_W, ENT_H,
        boxstyle="round,pad=0.03", linewidth=1.8,
        edgecolor='black', facecolor='white', zorder=10)
    ax.add_patch(rect)
    ax.text(cx, cy, name, fontsize=FONT_ENT, fontweight='bold',
            ha='center', va='center', zorder=11)

def _attr_ellipse_size(name):
    tw = sum(0.14 if ord(c) > 127 else 0.08 for c in name)
    ew = max(tw + 0.35, 0.75)
    return ew, ATTR_EH

def draw_attribute(cx, cy, name, is_pk=False):
    ew, eh = _attr_ellipse_size(name)
    ell = mpatches.Ellipse((cx, cy), ew, eh, linewidth=1.0,
                           edgecolor='black', facecolor='white', zorder=8)
    ax.add_patch(ell)
    ax.text(cx, cy, name, fontsize=FONT_ATTR,
            ha='center', va='center',
            fontweight='bold' if is_pk else 'normal', zorder=11)
    if is_pk:
        t = ax.text(cx, cy, name, fontsize=FONT_ATTR, fontweight='bold',
                    ha='center', va='center', zorder=11)
        bb = t.get_window_extent(renderer=fig.canvas.get_renderer())
        inv = ax.transData.inverted()
        p0, p1 = inv.transform((bb.x0, bb.y0)), inv.transform((bb.x1, bb.y1))
        t.remove()
        ax.text(cx, cy, name, fontsize=FONT_ATTR, fontweight='bold',
                ha='center', va='center', zorder=11)
        ax.plot([p0[0]+0.01, p1[0]-0.01], [p0[1]-0.015, p0[1]-0.015],
                color='black', linewidth=0.7, zorder=12)
    return ew/2, eh/2   # half-widths for overlap checking

def draw_relationship(cx, cy, name, hw=DIAM_HW, hh=DIAM_HH):
    diamond = mpatches.Polygon(
        [(cx, cy+hh), (cx+hw, cy), (cx, cy-hh), (cx-hw, cy)],
        closed=True, linewidth=1.3,
        edgecolor='black', facecolor='white', zorder=9)
    ax.add_patch(diamond)
    ax.text(cx, cy, name, fontsize=FONT_REL,
            ha='center', va='center', zorder=11)

def line(x1, y1, x2, y2, lw=0.9):
    ax.plot([x1, x2], [y1, y2], color='black', linewidth=lw, zorder=3)

def routed(pts, lw=0.9):
    ax.plot([p[0] for p in pts], [p[1] for p in pts],
            color='black', linewidth=lw, zorder=3)

def card(x, y, text):
    ax.text(x, y, text, fontsize=FONT_CARD, fontweight='bold',
            ha='center', va='center',
            bbox=dict(boxstyle='round,pad=0.06', fc='white', ec='none'),
            zorder=13)

def attr_conn(cx, cy, ex, ey):
    line(cx, cy, ex, ey, lw=0.5)


# ═══════════════════════════════════════════════════════════════════════════
#  ENTITY POSITIONS  (3-row grid)
# ═══════════════════════════════════════════════════════════════════════════
#  Row 1 (y=10):  系统用户(1.5)   兼职分类(4.1)   系统字典(6.7)
#  Row 2 (y=6.5): 用户档案(1.5)   兼职信息(4.1)
#  Row 3 (y=3.0): 求职申请(1.5)   工作订单(4.1)   评价信息(6.7)
#
#  Edges: L=cx-0.675  R=cx+0.675  T=cy+0.2  B=cy-0.2

draw_entity(1.5, 10.0, '系统用户')
draw_entity(4.1, 10.0, '兼职分类')
draw_entity(6.7, 10.0, '系统字典')
draw_entity(1.5, 6.5,  '用户档案')
draw_entity(4.1, 6.5,  '兼职信息')
draw_entity(1.5, 3.0,  '求职申请')
draw_entity(4.1, 3.0,  '工作订单')
draw_entity(6.7, 3.0,  '评价信息')

# ═══════════════════════════════════════════════════════════════════════════
#  ATTRIBUTES — placed carefully on outer sides
# ═══════════════════════════════════════════════════════════════════════════
# Route map:
#   R1: vertical x=1.5  (row1→row2)
#   R2: right corridor x=3.0  (系统用户→兼职信息)
#   R3: left corridor x=-0.8  (系统用户→求职申请)
#   R4: vertical x=4.1  (row1→row2)
#   R5: diagonal  (兼职信息→求职申请)
#   R6: horizontal y=3.0  (求职申请→工作订单) small diamond
#   R7: horizontal y=3.0  (工作订单→评价信息) small diamond

# ── 系统用户 (1.5, 10) — attrs on TOP, spread vertically ──────────────
draw_attribute(1.5, 11.3, '用户ID', is_pk=True);  attr_conn(1.5, 11.3, 1.5, 10.2)
draw_attribute(0.4, 10.9, '用户名');              attr_conn(0.4, 10.9, 1.5, 10.2)
draw_attribute(2.6, 10.9, '角色');                attr_conn(2.6, 10.9, 1.5, 10.2)

# ── 兼职分类 (4.1, 10) — attrs on TOP ──────────────────────────────────
draw_attribute(3.5, 10.9, '分类ID', is_pk=True);  attr_conn(3.5, 10.9, 4.1, 10.2)
draw_attribute(4.8, 10.9, '分类名称');             attr_conn(4.8, 10.9, 4.1, 10.2)

# ── 系统字典 (6.7, 10) — attrs on TOP ──────────────────────────────────
draw_attribute(6.2, 10.9, '字典ID', is_pk=True);  attr_conn(6.2, 10.9, 6.7, 10.2)
draw_attribute(7.4, 10.9, '字典编码');             attr_conn(7.4, 10.9, 6.7, 10.2)

# ── 用户档案 (1.5, 6.5) — attrs on BOTTOM (avoid R3 corridor & 提交diamond) ─
draw_attribute(0.8, 5.7, '档案ID', is_pk=True);   attr_conn(0.8, 5.7, 1.5, 6.5)
draw_attribute(1.5, 5.4, '真实姓名');              attr_conn(1.5, 5.4, 1.5, 6.5)
draw_attribute(2.2, 5.7, '学校');                 attr_conn(2.2, 5.7, 1.5, 6.5)

# ── 兼职信息 (4.1, 6.5) — attrs on RIGHT ───────────────────────────────
draw_attribute(5.7, 7.2, '兼职ID', is_pk=True);    attr_conn(5.7, 7.2, 4.1, 6.5)
draw_attribute(5.7, 6.5, '岗位名称');              attr_conn(5.7, 6.5, 4.1, 6.5)
draw_attribute(5.7, 5.8, '薪资');                  attr_conn(5.7, 5.8, 4.1, 6.5)

# ── 求职申请 (1.5, 3) — attrs on BOTTOM, spread in V ───────────────────
draw_attribute(0.4, 1.8, '申请ID', is_pk=True);   attr_conn(0.4, 1.8, 1.5, 2.8)
draw_attribute(1.5, 1.5, '申请时间');               attr_conn(1.5, 1.5, 1.5, 2.8)
draw_attribute(2.5, 1.5, '状态');                  attr_conn(2.5, 1.5, 1.5, 2.8)

# ── 工作订单 (4.1, 3) — attrs on BOTTOM, spread in V ───────────────────
draw_attribute(3.3, 1.6, '订单ID', is_pk=True);   attr_conn(3.3, 1.6, 4.1, 2.8)
draw_attribute(4.1, 1.4, '金额');                  attr_conn(4.1, 1.4, 4.1, 2.8)
draw_attribute(5.0, 1.8, '结算状态');              attr_conn(5.0, 1.8, 4.1, 2.8)

# ── 评价信息 (6.7, 3) — attrs on RIGHT ─────────────────────────────────
draw_attribute(8.1, 3.7, '评价ID', is_pk=True);   attr_conn(8.1, 3.7, 6.7, 3.0)
draw_attribute(8.1, 3.0, '评分');                  attr_conn(8.1, 3.0, 6.7, 3.0)
draw_attribute(8.1, 2.3, '评论');                  attr_conn(8.1, 2.3, 6.7, 3.0)


# ═══════════════════════════════════════════════════════════════════════════
#  RELATIONSHIPS  (diamonds + lines + cardinality)
# ═══════════════════════════════════════════════════════════════════════════

# ── R1: 系统用户 ↔ 用户档案  1:1 "拥有" — vertical ─────────────────────
draw_relationship(1.5, 8.25, '拥有')
line(1.5, 9.80, 1.5, 8.53)        # ent1 bottom → diamond top
line(1.5, 7.97, 1.5, 6.70)        # diamond bottom → ent2 top
card(1.65, 9.65, '1')
card(1.65, 6.85, '1')

# ── R2: 系统用户 → 兼职信息  1:n "发布" — routed right corridor ───────
draw_relationship(3.0, 8.25, '发布')
routed([(2.175, 10.0), (3.0, 10.0), (3.0, 8.53)])      # ent1 right → diamond top
routed([(3.0, 7.97), (3.0, 6.50), (3.425, 6.50)])       # diamond bottom → ent2 left
card(3.15, 9.90, '1')
card(3.55, 6.65, 'n')

# ── R3: 系统用户 → 求职申请  1:n "提交" — routed LEFT corridor ─────────
draw_relationship(-0.8, 6.5, '提交')
routed([(0.825, 10.0), (-0.8, 10.0), (-0.8, 6.78)])     # ent1 left → diamond top
routed([(-0.8, 6.22), (-0.8, 3.0), (0.825, 3.0)])        # diamond bottom → ent3 left
card(-0.65, 9.90, '1')
card(-0.65, 3.30, 'n')

# ── R4: 兼职分类 → 兼职信息  1:n "属于" — vertical ──────────────────────
draw_relationship(4.1, 8.25, '属于')
line(4.1, 9.80, 4.1, 8.53)
line(4.1, 7.97, 4.1, 6.70)
card(4.25, 9.65, '1')
card(4.25, 6.85, 'n')

# ── R5: 兼职信息 → 求职申请  1:n "接收" — diagonal (no crossing with R6) ──
draw_relationship(2.8, 4.75, '接收')
line(4.1, 6.30, 2.8, 5.03)         # ent2 bottom → diamond top-right area
line(2.8, 4.47, 1.5, 3.20)         # diamond bottom-left → ent3 top
card(4.25, 6.18, '1')
card(1.35, 3.35, 'n')

# ── R6: 求职申请 → 工作订单  1:1 "生成" — horizontal, SMALL diamond ───
draw_relationship(2.8, 3.0, '生成', hw=DIAM_S_HW, hh=DIAM_S_HH)
line(2.175, 3.0, 2.42, 3.0)        # ent3 right → diamond left
line(3.18, 3.0, 3.425, 3.0)        # diamond right → ent4 left
card(2.30, 2.78, '1')
card(3.30, 2.78, '1')

# ── R7: 工作订单 → 评价信息  1:1 "产生" — horizontal, SMALL diamond ───
draw_relationship(5.4, 3.0, '产生', hw=DIAM_S_HW, hh=DIAM_S_HH)
line(4.775, 3.0, 5.02, 3.0)        # ent4 right → diamond left
line(5.78, 3.0, 6.025, 3.0)        # diamond right → ent5 left
card(4.90, 2.78, '1')
card(5.90, 2.78, '1')


# ═══════════════════════════════════════════════════════════════════════════
#  Title
# ═══════════════════════════════════════════════════════════════════════════
ax.text(3.8, 12.0, '校园兼职平台E-R实体关系图',
        fontsize=FONT_TITLE, fontweight='bold',
        ha='center', va='center', zorder=15)


# ═══════════════════════════════════════════════════════════════════════════
#  Save
# ═══════════════════════════════════════════════════════════════════════════
plt.tight_layout(pad=0.5)
out = '/home/z/my-project/Xiao-Yi-Pin/download/thesis_images/er_diagram.png'
fig.savefig(out, dpi=300, bbox_inches='tight', facecolor='white', edgecolor='none')
plt.close(fig)

from PIL import Image
img = Image.open(out)
print(f"Saved -> {out}")
print(f"Size: {img.size[0]} x {img.size[1]} px  ({img.size[0]/300:.2f} x {img.size[1]/300:.2f} in)")
print(f"A4:   8.27 x 11.69 in")
