#!/usr/bin/env python3
"""
E-R Diagram — Standard ER Notation (v4, FULL ATTRIBUTES)
  矩形 = 实体
  椭圆 = 属性 (PK下划线)
  菱形 = 关系
  包含所有业务属性, 基于 sql/campus_job.sql 完整字段
  300 DPI, 黑白, 适合论文
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.font_manager import fontManager

fontManager.addfont('/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc')
plt.rcParams['font.sans-serif'] = ['WenQuanYi Zen Hei']
plt.rcParams['axes.unicode_minus'] = False

# ── Figure: A3 landscape for more room ─────────────────────────────────
FIG_W, FIG_H = 16.54, 11.69   # A3 landscape in inches
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H), dpi=300)
ax.set_xlim(-3.5, 18.0)
ax.set_ylim(-2.0, 14.0)
ax.set_aspect('equal')
ax.axis('off')
fig.patch.set_facecolor('white')

# ── Sizes ──────────────────────────────────────────────────────────────
ENT_W, ENT_H   = 1.50, 0.42
ATTR_EH        = 0.28
DIAM_HW, DIAM_HH = 0.55, 0.30   # standard diamond
DIAM_S_HW, DIAM_S_HH = 0.42, 0.22  # small diamond

FONT_ENT   = 9.0
FONT_ATTR  = 6.5
FONT_REL   = 7.5
FONT_CARD  = 6.5
FONT_TITLE = 14


# ═══════════════════════════════════════════════════════════════════════════
#  Drawing helpers
# ═══════════════════════════════════════════════════════════════════════════

def draw_entity(cx, cy, name):
    rect = mpatches.FancyBboxPatch(
        (cx - ENT_W/2, cy - ENT_H/2), ENT_W, ENT_H,
        boxstyle="round,pad=0.03", linewidth=2.0,
        edgecolor='black', facecolor='white', zorder=10)
    ax.add_patch(rect)
    ax.text(cx, cy, name, fontsize=FONT_ENT, fontweight='bold',
            ha='center', va='center', zorder=11)

def _attr_ellipse_size(name):
    tw = sum(0.13 if ord(c) > 127 else 0.075 for c in name)
    ew = max(tw + 0.30, 0.70)
    return ew, ATTR_EH

def draw_attribute(cx, cy, name, is_pk=False):
    ew, eh = _attr_ellipse_size(name)
    ell = mpatches.Ellipse((cx, cy), ew, eh, linewidth=0.9,
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

def draw_relationship(cx, cy, name, hw=DIAM_HW, hh=DIAM_HH):
    diamond = mpatches.Polygon(
        [(cx, cy+hh), (cx+hw, cy), (cx, cy-hh), (cx-hw, cy)],
        closed=True, linewidth=1.4,
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
    line(cx, cy, ex, ey, lw=0.45)

def attrs_row(cx, cy, ent_cx, ent_cy, names, pk_index=0, direction='right', spacing=0.75):
    """Draw a row of attribute ellipses connected to entity."""
    n = len(names)
    for i, name in enumerate(names):
        if direction == 'right':
            ax_pos = cx + i * spacing
            ay_pos = cy
            conn_ex = ent_cx + ENT_W / 2
            conn_ey = ent_cy
        elif direction == 'left':
            ax_pos = cx - i * spacing
            ay_pos = cy
            conn_ex = ent_cx - ENT_W / 2
            conn_ey = ent_cy
        elif direction == 'top':
            start_x = cx - (n - 1) * spacing / 2
            ax_pos = start_x + i * spacing
            ay_pos = cy
            conn_ex = ent_cx
            conn_ey = ent_cy + ENT_H / 2
        elif direction == 'bottom':
            start_x = cx - (n - 1) * spacing / 2
            ax_pos = start_x + i * spacing
            ay_pos = cy
            conn_ex = ent_cx
            conn_ey = ent_cy - ENT_H / 2
        elif direction == 'top-left':
            start_x = cx - (n - 1) * spacing / 2 - spacing * 0.3
            ax_pos = start_x + i * spacing
            ay_pos = cy
            conn_ex = ent_cx - ENT_W / 4
            conn_ey = ent_cy + ENT_H / 2
        elif direction == 'top-right':
            start_x = cx + (n - 1) * spacing / 2 + spacing * 0.3
            ax_pos = start_x - i * spacing
            ay_pos = cy
            conn_ex = ent_cx + ENT_W / 4
            conn_ey = ent_cy + ENT_H / 2
        elif direction == 'bottom-left':
            start_x = cx - (n - 1) * spacing / 2 - spacing * 0.3
            ax_pos = start_x + i * spacing
            ay_pos = cy
            conn_ex = ent_cx - ENT_W / 4
            conn_ey = ent_cy - ENT_H / 2
        elif direction == 'bottom-right':
            start_x = cx + (n - 1) * spacing / 2 + spacing * 0.3
            ax_pos = start_x - i * spacing
            ay_pos = cy
            conn_ex = ent_cx + ENT_W / 4
            conn_ey = ent_cy - ENT_H / 2
        else:
            ax_pos, ay_pos = cx, cy
            conn_ex, conn_ey = ent_cx, ent_cy

        is_pk = (i == pk_index)
        draw_attribute(ax_pos, ay_pos, name, is_pk=is_pk)
        attr_conn(ax_pos, ay_pos, conn_ex, conn_ey)


# ═══════════════════════════════════════════════════════════════════════════
#  ENTITY POSITIONS  (3-row grid, wider spacing)
# ═══════════════════════════════════════════════════════════════════════════
#
#  Row 1 (y=11): 系统用户(3)    兼职分类(8)     系统字典(14)
#  Row 2 (y=7):  用户档案(3)    兼职信息(8)
#  Row 3 (y=3):  求职申请(3)    工作订单(8)     评价信息(14)
#

EX1, EY1 = 3.0, 11.0    # 系统用户
EX2, EY2 = 8.5, 11.0    # 兼职分类
EX3, EY3 = 14.0, 11.0   # 系统字典

EX4, EY4 = 3.0, 7.0     # 用户档案
EX5, EY5 = 8.5, 7.0     # 兼职信息

EX6, EY6 = 3.0, 3.0     # 求职申请
EX7, EY7 = 8.5, 3.0     # 工作订单
EX8, EY8 = 14.0, 3.0    # 评价信息

draw_entity(EX1, EY1, '系统用户')
draw_entity(EX2, EY2, '兼职分类')
draw_entity(EX3, EY3, '系统字典')
draw_entity(EX4, EY4, '用户档案')
draw_entity(EX5, EY5, '兼职信息')
draw_entity(EX6, EY6, '求职申请')
draw_entity(EX7, EY7, '工作订单')
draw_entity(EX8, EY8, '评价信息')

# ═══════════════════════════════════════════════════════════════════════════
#  ATTRIBUTES — ALL fields from SQL (excluding technical: deleted, version,
#  create_time, update_time)
# ═══════════════════════════════════════════════════════════════════════════
#
#  Layout corridors:
#    LEFT  corridor x < 1.5  (for R3 route from 系统用户→求职申请)
#    MID-L corridor x ~5.5-6.0 (for R2 route from 系统用户→兼职信息)
#    MID   corridor x ~8.5 (entity column 2 center)
#    MID-R corridor x ~11.5 (between col2 and col3)
#    RIGHT corridor x > 15.5 (for attributes on right side)

SP = 0.82  # spacing between attribute ellipses

# ── 系统用户 (3, 11) — 7 attrs ─────────────────────────────────────────
#    TOP:  用户ID(PK), 用户名, 密码哈希
#    TOP-R: 手机号, 邮箱, 角色类型, 状态
attrs_row(EX1, EY1 + 1.2, EX1, EY1,
          ['用户ID', '用户名', '密码哈希'], pk_index=0, direction='top', spacing=SP)
attrs_row(EX1 + 2.5, EY1 + 0.8, EX1, EY1,
          ['手机号', '邮箱', '角色类型', '状态'], pk_index=-1, direction='top-right', spacing=SP)

# ── 兼职分类 (8.5, 11) — 5 attrs ──────────────────────────────────────
#    TOP:  分类ID(PK), 分类名称, 父级ID, 排序, 状态
attrs_row(EX2, EY2 + 1.2, EX2, EY2,
          ['分类ID', '分类名称', '父级ID', '排序', '状态'],
          pk_index=0, direction='top', spacing=SP)

# ── 系统字典 (14, 11) — 6 attrs ───────────────────────────────────────
#    TOP:  字典ID(PK), 字典类型, 字典编码
#    TOP-R: 字典标签, 排序, 状态
attrs_row(EX3 - 1.2, EY3 + 1.2, EX3, EY3,
          ['字典ID', '字典类型', '字典编码'], pk_index=0, direction='top-left', spacing=SP)
attrs_row(EX3 + 1.2, EY3 + 1.2, EX3, EY3,
          ['字典标签', '排序', '状态'], pk_index=-1, direction='top-right', spacing=SP)

# ── 用户档案 (3, 7) — 13 attrs ────────────────────────────────────────
#    LEFT vertical (x=1.0): first 7 attrs (avoids R3 corridor at x=-1.5)
#    BOTTOM two rows of 3: remaining 6 attrs
profile_left = ['档案ID', '用户ID', '真实姓名', '身份证哈希', '性别', '学校', '专业']
profile_bot1 = ['年级', '余额', '信用积分']
profile_bot2 = ['头像URL', '认证状态', '身份证图片']
pl_start = EY4 + len(profile_left) * 0.37 / 2
for i, name in enumerate(profile_left):
    is_pk = (i == 0)
    draw_attribute(EX4 - 2.0, pl_start - i * 0.37, name, is_pk=is_pk)
    attr_conn(EX4 - 2.0, pl_start - i * 0.37, EX4 - ENT_W/2, EY4)
# bottom row 1
pb1_sp = 1.2
pb1_sx = EX4 - (len(profile_bot1) - 1) * pb1_sp / 2
for i, name in enumerate(profile_bot1):
    draw_attribute(pb1_sx + i * pb1_sp, EY4 - 1.3, name)
    attr_conn(pb1_sx + i * pb1_sp, EY4 - 1.3, EX4, EY4 - ENT_H/2)
# bottom row 2
pb2_sp = 1.2
pb2_sx = EX4 - (len(profile_bot2) - 1) * pb2_sp / 2
for i, name in enumerate(profile_bot2):
    draw_attribute(pb2_sx + i * pb2_sp, EY4 - 2.2, name)
    attr_conn(pb2_sx + i * pb2_sp, EY4 - 2.2, EX4, EY4 - ENT_H/2)

# ── 兼职信息 (8.5, 7) — 16 attrs ──────────────────────────────────────
#    RIGHT (vertical): 兼职ID(PK), 发布者ID, 分类ID, 岗位名称,
#                      工作描述, 工作地点, 薪资类型, 薪资金额,
#                      开始时间, 结束时间, 招聘人数, 已录用人数,
#                      审核状态, 审核备注
#    BOTTOM: 状态
right_attrs = ['兼职ID', '发布者ID', '分类ID', '岗位名称',
               '工作描述', '工作地点', '薪资类型', '薪资金额',
               '开始时间', '结束时间', '招聘人数', '已录用人数',
               '审核状态', '审核备注', '状态']
ry_start = EY5 + len(right_attrs) * 0.34 / 2
for i, name in enumerate(right_attrs):
    is_pk = (i == 0)
    draw_attribute(EX5 + 2.0, ry_start - i * 0.34, name, is_pk=is_pk)
    attr_conn(EX5 + 2.0, ry_start - i * 0.34, EX5 + ENT_W/2, EY5)

# ── 求职申请 (3, 3) — 8 attrs ─────────────────────────────────────────
#    LEFT (vertical): 申请ID(PK), 兼职ID, 申请者ID, 简历URL,
#                     申请状态, 申请时间, 拒绝原因, 审核备注
left_attrs = ['申请ID', '兼职ID', '申请者ID', '简历URL',
              '申请状态', '申请时间', '拒绝原因', '审核备注']
ly_start = EY6 + len(left_attrs) * 0.34 / 2
for i, name in enumerate(left_attrs):
    is_pk = (i == 0)
    draw_attribute(EX6 - 2.0, ly_start - i * 0.34, name, is_pk=is_pk)
    attr_conn(EX6 - 2.0, ly_start - i * 0.34, EX6 - ENT_W/2, EY6)

# ── 工作订单 (8.5, 3) — 10 attrs ──────────────────────────────────────
#    BOTTOM: 订单ID(PK), 申请ID, 学生ID, 雇主ID, 岗位ID
#    BOTTOM-2: 订单金额, 支付状态, 结算状态, 开始日期, 结束日期
attrs_row(EX7, EY7 - 1.0, EX7, EY7,
          ['订单ID', '申请ID', '学生ID', '雇主ID', '岗位ID'],
          pk_index=0, direction='bottom-left', spacing=SP)
attrs_row(EX7 + 2.0, EY7 - 1.8, EX7, EY7,
          ['订单金额', '支付状态', '结算状态', '开始日期', '结束日期'],
          pk_index=-1, direction='bottom-right', spacing=SP)

# ── 评价信息 (14, 3) — 7 attrs ────────────────────────────────────────
#    RIGHT (vertical): 评价ID(PK), 订单ID, 评价者ID, 被评价者ID,
#                      评价类型, 评分, 评论内容
r_attrs = ['评价ID', '订单ID', '评价者ID', '被评价者ID', '评价类型', '评分', '评论内容']
r_start = EY8 + len(r_attrs) * 0.34 / 2
for i, name in enumerate(r_attrs):
    is_pk = (i == 0)
    draw_attribute(EX8 + 2.0, r_start - i * 0.34, name, is_pk=is_pk)
    attr_conn(EX8 + 2.0, r_start - i * 0.34, EX8 + ENT_W/2, EY8)


# ═══════════════════════════════════════════════════════════════════════════
#  RELATIONSHIPS  (diamonds + lines + cardinality)
# ═══════════════════════════════════════════════════════════════════════════
#
#  Route corridors:
#    x = -3.0  (far left corridor for R3)
#    x = 5.8   (mid-left corridor for R2)
#    x = 3.0   (col1 center)
#    x = 5.5   (between col1 and col2, above row2)
#    x = 8.5   (col2 center)
#    x = 11.5  (between col2 and col3)
#    x = 14.0  (col3 center)

# ── R1: 系统用户 ↔ 用户档案  1:1 "拥有" — vertical x=3 ──────────────
draw_relationship(3.0, 9.0, '拥有')
line(3.0, EY1 - ENT_H/2, 3.0, 9.0 + DIAM_HH)         # ent1 bottom → diamond top
line(3.0, 9.0 - DIAM_HH, 3.0, EY4 + ENT_H/2)         # diamond bottom → ent2 top
card(3.20, EY1 - ENT_H/2 + 0.15, '1')
card(3.20, EY4 + ENT_H/2 - 0.15, '1')

# ── R2: 系统用户 → 兼职信息  1:n "发布" — via corridor x=5.8 ─────────
draw_relationship(5.8, 9.0, '发布')
routed([(EX1 + ENT_W/2, EY1), (5.8, EY1), (5.8, 9.0 + DIAM_HH)])
routed([(5.8, 9.0 - DIAM_HH), (5.8, EY5), (EX5 - ENT_W/2, EY5)])
card(5.95, EY1 + 0.15, '1')
card(EX5 - ENT_W/2 - 0.35, EY5 + 0.15, 'n')

# ── R3: 系统用户 → 求职申请  1:n "提交" — via far-left corridor x=-3 ─
draw_relationship(-1.5, 7.0, '提交')
routed([(EX1 - ENT_W/2, EY1), (-1.5, EY1), (-1.5, 7.0 + DIAM_HH)])
routed([(-1.5, 7.0 - DIAM_HH), (-1.5, EY6), (EX6 - ENT_W/2, EY6)])
card(-1.35, EY1 + 0.15, '1')
card(-1.35, EY6 + 0.15, 'n')

# ── R4: 兼职分类 → 兼职信息  1:n "属于" — vertical x=8.5 ─────────────
draw_relationship(8.5, 9.0, '属于')
line(8.5, EY2 - ENT_H/2, 8.5, 9.0 + DIAM_HH)
line(8.5, 9.0 - DIAM_HH, 8.5, EY5 + ENT_H/2)
card(8.70, EY2 - ENT_H/2 + 0.15, '1')
card(8.70, EY5 + ENT_H/2 - 0.15, 'n')

# ── R5: 兼职信息 → 求职申请  1:n "接收" — diagonal ────────────────────
draw_relationship(5.5, 5.0, '接收')
line(EX5 - ENT_W/2, EY5 - 0.10, 5.5 + DIAM_HW * 0.3, 5.0 + DIAM_HH)
line(5.5 - DIAM_HW * 0.3, 5.0 - DIAM_HH, EX6 + ENT_W/2, EY6 + 0.10)
card(EX5 - ENT_W/2 - 0.30, EY5 - 0.30, '1')
card(EX6 + ENT_W/2 + 0.30, EY6 + 0.25, 'n')

# ── R6: 求职申请 → 工作订单  1:1 "生成" — horizontal, SMALL diamond ───
draw_relationship(5.75, EY6, '生成', hw=DIAM_S_HW, hh=DIAM_S_HH)
line(EX6 + ENT_W/2, EY6, 5.75 - DIAM_S_HW, EY6)
line(5.75 + DIAM_S_HW, EY6, EX7 - ENT_W/2, EY7)
card(EX6 + ENT_W/2 + 0.15, EY6 - 0.30, '1')
card(EX7 - ENT_W/2 - 0.15, EY7 - 0.30, '1')

# ── R7: 工作订单 → 评价信息  1:1 "产生" — routed via y=1 ─────────────
draw_relationship(11.25, 3.0, '产生', hw=DIAM_S_HW, hh=DIAM_S_HH)
line(EX7 + ENT_W/2, EY7, 11.25 - DIAM_S_HW, EY7)
line(11.25 + DIAM_S_HW, EY7, EX8 - ENT_W/2, EY8)
card(EX7 + ENT_W/2 + 0.15, EY7 - 0.30, '1')
card(EX8 - ENT_W/2 - 0.15, EY8 - 0.30, '1')


# ═══════════════════════════════════════════════════════════════════════════
#  Title
# ═══════════════════════════════════════════════════════════════════════════
ax.text(7.5, 13.3, '校园兼职平台E-R实体关系图',
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
print(f"A4:   8.27 x 11.69 in  |  A3:  11.69 x 16.54 in")

# ═══════════════════════════════════════════════════════════════════════════
#  Attribute count verification
# ═══════════════════════════════════════════════════════════════════════════
attr_counts = {
    '系统用户': 7,   # id, username, password_hash, phone, email, role_type, status
    '用户档案': 13,  # id, user_id, real_name, id_card_hash, id_card_image, gender, university, major, grade, balance, credit_score, avatar_url, verified_status
    '兼职分类': 5,   # id, name, parent_id, sort_order, status
    '兼职信息': 16,  # id, publisher_id, category_id, title, description, location, salary_type, salary_amount, start_time, end_time, recruit_num, hired_num, audit_status, audit_remark, status
    '求职申请': 8,   # id, job_id, applicant_id, resume_url, status, apply_time, reject_reason, review_remark
    '工作订单': 10,  # id, application_id, student_id, employer_id, job_id, amount, pay_status, settlement_status, start_date, end_date
    '评价信息': 7,   # id, order_id, reviewer_id, target_id, type, rating, comment
    '系统字典': 6,   # id, dict_type, dict_code, dict_label, sort_order, status
}
total = sum(attr_counts.values())
print(f"\n=== Attribute Count Summary ===")
for name, count in attr_counts.items():
    print(f"  {name}: {count} attributes")
print(f"  TOTAL: {total} attributes (excluding deleted/version/create_time/update_time)")
