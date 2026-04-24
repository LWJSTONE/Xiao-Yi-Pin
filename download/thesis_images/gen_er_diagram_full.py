#!/usr/bin/env python3
"""
E-R Diagram — Standard ER Notation (v5, FULL ATTRIBUTES, A4 PORTRAIT)
  矩形 = 实体
  椭圆 = 属性 (PK下划线)
  菱形 = 关系
  包含所有业务属性, 基于 sql/campus_job.sql 完整字段
  300 DPI, 黑白, 适合论文
  A4 Portrait (8.27 × 11.69 inches) — 直接插入Word无需缩放
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.font_manager import fontManager

fontManager.addfont('/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc')
plt.rcParams['font.sans-serif'] = ['WenQuanYi Zen Hei']
plt.rcParams['axes.unicode_minus'] = False

# ── Figure: A4 Portrait, native output for Word insertion ────────────
FIG_W, FIG_H = 8.27, 11.69   # A4 portrait in inches
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H), dpi=300)
ax.set_xlim(-2.0, 8.5)
ax.set_ylim(-0.5, 12.5)
ax.set_aspect('equal')
ax.axis('off')
fig.patch.set_facecolor('white')

# ── Sizes ─────────────────────────────────────────────────────────────
ENT_W, ENT_H   = 1.05, 0.30
ATTR_EH        = 0.20
DIAM_HW, DIAM_HH = 0.40, 0.22   # standard diamond
DIAM_S_HW, DIAM_S_HH = 0.30, 0.16  # small diamond

FONT_ENT   = 7.0
FONT_ATTR  = 5.0
FONT_REL   = 6.0
FONT_CARD  = 5.0
FONT_TITLE = 11


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
    tw = sum(0.095 if ord(c) > 127 else 0.055 for c in name)
    ew = max(tw + 0.22, 0.50)
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
#  ENTITY POSITIONS  (A4 Portrait layout)
# ═══════════════════════════════════════════════════════════════════════════
#
#  Row 1 (y=10.5): 系统用户(1.2)    兼职分类(4.2)    系统字典(6.8)
#  Row 2 (y=7.0):  用户档案(1.2)    兼职信息(4.2)
#  Row 3 (y=4.0):  求职申请(1.8)    工作订单(5.2)
#  Row 4 (y=1.2):                  评价信息(5.2)
#

EX1, EY1 = 1.2, 10.5     # 系统用户
EX2, EY2 = 4.2, 10.5     # 兼职分类
EX3, EY3 = 6.8, 10.5     # 系统字典

EX4, EY4 = 1.2, 7.0      # 用户档案
EX5, EY5 = 4.2, 7.0      # 兼职信息

EX6, EY6 = 1.8, 4.0      # 求职申请
EX7, EY7 = 5.2, 4.0      # 工作订单

EX8, EY8 = 5.2, 1.2      # 评价信息

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

SP = 0.68  # spacing between attribute ellipses (0.55→0.68 fix overlaps)

# ── 系统用户 (1.2, 10.5) — 7 attrs ──────────────────────────────────
#    TOP row 1:  用户ID(PK), 用户名, 密码哈希
#    TOP row 2:  手机号, 邮箱, 角色类型, 状态
attrs_row(EX1, EY1 + 1.0, EX1, EY1,
          ['用户ID', '用户名', '密码哈希'], pk_index=0, direction='top', spacing=SP)
attrs_row(EX1, EY1 + 1.6, EX1, EY1,
          ['手机号', '邮箱', '角色类型', '状态'], pk_index=-1, direction='top', spacing=SP)

# ── 兼职分类 (4.2, 10.5) — 5 attrs ────────────────────────────────
#    TOP row 1:  分类ID(PK), 分类名称, 父级ID
#    TOP row 2:  排序, 状态
attrs_row(EX2, EY2 + 1.0, EX2, EY2,
          ['分类ID', '分类名称', '父级ID'],
          pk_index=0, direction='top', spacing=SP)
attrs_row(EX2, EY2 + 1.6, EX2, EY2,
          ['排序', '状态'],
          pk_index=-1, direction='top', spacing=SP)

# ── 系统字典 (6.8, 10.5) — 6 attrs ────────────────────────────────
#    TOP row 1:  字典ID(PK), 字典类型, 字典编码
#    TOP row 2:  字典标签, 排序, 状态
attrs_row(EX3, EY3 + 1.0, EX3, EY3,
          ['字典ID', '字典类型', '字典编码'], pk_index=0, direction='top', spacing=SP)
attrs_row(EX3, EY3 + 1.7, EX3, EY3,
          ['字典标签', '排序', '状态'], pk_index=-1, direction='top', spacing=SP)

# ── 用户档案 (1.2, 7.0) — 13 attrs ────────────────────────────────
#    TOP row 1:  档案ID(PK), 用户ID, 真实姓名
#    TOP row 2:  身份证哈希, 性别, 学校
#    LEFT vertical: 专业, 年级, 余额, 信用积分
#    BOTTOM row:    头像URL, 认证状态, 身份证图片
profile_top1 = ['档案ID', '用户ID', '真实姓名']
profile_top2 = ['身份证哈希', '性别', '学校']
profile_left = ['专业', '年级', '余额', '信用积分']
profile_bot = ['头像URL', '认证状态', '身份证图片']

attrs_row(EX4, EY4 + 1.1, EX4, EY4,
          profile_top1, pk_index=0, direction='top', spacing=SP)
attrs_row(EX4, EY4 + 1.7, EX4, EY4,
          profile_top2, pk_index=-1, direction='top', spacing=SP)

# Left vertical attrs
pl_start = EY4 + len(profile_left) * 0.26 / 2
for i, name in enumerate(profile_left):
    draw_attribute(EX4 - 1.3, pl_start - i * 0.26, name)
    attr_conn(EX4 - 1.3, pl_start - i * 0.26, EX4 - ENT_W/2, EY4)

# Bottom row
pb_sp = 0.75
pb_sx = EX4 - (len(profile_bot) - 1) * pb_sp / 2
for i, name in enumerate(profile_bot):
    draw_attribute(pb_sx + i * pb_sp, EY4 - 1.05, name)
    attr_conn(pb_sx + i * pb_sp, EY4 - 1.05, EX4, EY4 - ENT_H/2)

# ── 兼职信息 (4.2, 7.0) — 15 attrs ────────────────────────────────
#    RIGHT vertical (8): 兼职ID(PK), 发布者ID, 分类ID, 岗位名称,
#                         工作描述, 工作地点, 薪资类型, 薪资金额
#    BOTTOM row 1 (4):   开始时间, 结束时间, 招聘人数, 已录用人数
#    BOTTOM row 2 (3):   审核状态, 审核备注, 状态
right_attrs = ['兼职ID', '发布者ID', '分类ID', '岗位名称',
               '工作描述', '工作地点', '薪资类型', '薪资金额']
ry_start = EY5 + len(right_attrs) * 0.24 / 2
for i, name in enumerate(right_attrs):
    is_pk = (i == 0)
    draw_attribute(EX5 + 1.5, ry_start - i * 0.24, name, is_pk=is_pk)
    attr_conn(EX5 + 1.5, ry_start - i * 0.24, EX5 + ENT_W/2, EY5)

# Bottom rows
bot1 = ['开始时间', '结束时间', '招聘人数', '已录用人数']
b1_sp = 0.70
b1_sx = EX5 - (len(bot1) - 1) * b1_sp / 2
for i, name in enumerate(bot1):
    draw_attribute(b1_sx + i * b1_sp, EY5 - 1.05, name)
    attr_conn(b1_sx + i * b1_sp, EY5 - 1.05, EX5, EY5 - ENT_H/2)

bot2 = ['审核状态', '审核备注', '状态']
b2_sp = 0.70
b2_sx = EX5 - (len(bot2) - 1) * b2_sp / 2
for i, name in enumerate(bot2):
    draw_attribute(b2_sx + i * b2_sp, EY5 - 1.7, name)
    attr_conn(b2_sx + i * b2_sp, EY5 - 1.7, EX5, EY5 - ENT_H/2)

# ── 求职申请 (1.8, 4.0) — 8 attrs ─────────────────────────────────
#    TOP:           申请ID(PK), 兼职ID, 申请者ID, 简历URL
#    BOTTOM row 1:  申请状态, 申请时间, 拒绝原因
#    BOTTOM row 2:  审核备注
attrs_row(EX6, EY6 + 1.0, EX6, EY6,
          ['申请ID', '兼职ID', '申请者ID', '简历URL'],
          pk_index=0, direction='top', spacing=SP)
attrs_row(EX6, EY6 - 1.0, EX6, EY6,
          ['申请状态', '申请时间', '拒绝原因'],
          pk_index=-1, direction='bottom', spacing=SP)
attrs_row(EX6, EY6 - 1.6, EX6, EY6,
          ['审核备注'],
          pk_index=-1, direction='bottom', spacing=SP)

# ── 工作订单 (5.2, 4.0) — 10 attrs ────────────────────────────────
#    TOP row 1:      订单ID(PK), 申请ID, 学生ID
#    TOP row 2:      雇主ID, 岗位ID
#    BOTTOM row 1:   订单金额, 支付状态, 结算状态
#    BOTTOM row 2:   开始日期, 结束日期
attrs_row(EX7, EY7 + 1.0, EX7, EY7,
          ['订单ID', '申请ID', '学生ID'], pk_index=0, direction='top', spacing=SP)
attrs_row(EX7, EY7 + 1.6, EX7, EY7,
          ['雇主ID', '岗位ID'], pk_index=-1, direction='top', spacing=SP)
attrs_row(EX7, EY7 - 1.0, EX7, EY7,
          ['订单金额', '支付状态', '结算状态'],
          pk_index=-1, direction='bottom', spacing=SP)
attrs_row(EX7, EY7 - 1.6, EX7, EY7,
          ['开始日期', '结束日期'],
          pk_index=-1, direction='bottom', spacing=SP)

# ── 评价信息 (5.2, 1.2) — 7 attrs ─────────────────────────────────
#    TOP:           评价ID(PK), 订单ID, 评价者ID
#    RIGHT vertical: 被评价者ID, 评价类型, 评分, 评论内容
attrs_row(EX8, EY8 + 1.0, EX8, EY8,
          ['评价ID', '订单ID', '评价者ID'],
          pk_index=0, direction='top', spacing=SP)

r_attrs = ['被评价者ID', '评价类型', '评分', '评论内容']
r_start = EY8 + len(r_attrs) * 0.24 / 2
for i, name in enumerate(r_attrs):
    draw_attribute(EX8 + 1.5, r_start - i * 0.24, name)
    attr_conn(EX8 + 1.5, r_start - i * 0.24, EX8 + ENT_W/2, EY8)


# ═══════════════════════════════════════════════════════════════════════════
#  RELATIONSHIPS  (diamonds + lines + cardinality)
# ═══════════════════════════════════════════════════════════════════════════
#
#  Route corridors:
#    x = -1.5  (far left corridor for R3)
#    x = 2.5   (mid corridor for R2)
#    x = 6.5   (right corridor for R7)

# ── R1: 系统用户 ↔ 用户档案  1:1 "拥有" — vertical ─────────────────
draw_relationship(1.2, 8.75, '拥有')
line(1.2, EY1 - ENT_H/2, 1.2, 8.75 + DIAM_HH)
line(1.2, 8.75 - DIAM_HH, 1.2, EY4 + ENT_H/2)
card(1.38, EY1 - ENT_H/2 + 0.12, '1')
card(1.38, EY4 + ENT_H/2 - 0.12, '1')

# ── R2: 系统用户 → 兼职信息  1:n "发布" — via mid corridor ─────────
draw_relationship(2.5, 8.75, '发布')
routed([(EX1 + ENT_W/2, EY1), (2.5, EY1), (2.5, 8.75 + DIAM_HH)])
routed([(2.5, 8.75 - DIAM_HH), (2.5, EY5), (EX5 - ENT_W/2, EY5)])
card(2.62, EY1 + 0.12, '1')
card(EX5 - ENT_W/2 - 0.25, EY5 + 0.12, 'n')

# ── R3: 系统用户 → 求职申请  1:n "提交" — via far-left corridor ────
draw_relationship(-1.2, 7.25, '提交')
routed([(EX1 - ENT_W/2, EY1), (-1.2, EY1), (-1.2, 7.25 + DIAM_HH)])
routed([(-1.2, 7.25 - DIAM_HH), (-1.2, EY6), (EX6 - ENT_W/2, EY6)])
card(-1.05, EY1 + 0.12, '1')
card(-1.05, EY6 + 0.12, 'n')

# ── R4: 兼职分类 → 兼职信息  1:n "属于" — vertical ─────────────────
draw_relationship(4.2, 8.75, '属于')
line(4.2, EY2 - ENT_H/2, 4.2, 8.75 + DIAM_HH)
line(4.2, 8.75 - DIAM_HH, 4.2, EY5 + ENT_H/2)
card(4.38, EY2 - ENT_H/2 + 0.12, '1')
card(4.38, EY5 + ENT_H/2 - 0.12, 'n')

# ── R5: 兼职信息 → 求职申请  1:n "接收" — diagonal ─────────────────
draw_relationship(3.0, 5.5, '接收')
line(EX5 - ENT_W/2, EY5 - 0.08, 3.0 + DIAM_HW * 0.3, 5.5 + DIAM_HH)
line(3.0 - DIAM_HW * 0.3, 5.5 - DIAM_HH, EX6 + ENT_W/2, EY6 + 0.08)
card(EX5 - ENT_W/2 - 0.22, EY5 - 0.22, '1')
card(EX6 + ENT_W/2 + 0.22, EY6 + 0.18, 'n')

# ── R6: 求职申请 → 工作订单  1:1 "生成" — routed ──────────────────
draw_relationship(3.5, 4.0, '生成', hw=DIAM_S_HW, hh=DIAM_S_HH)
line(EX6 + ENT_W/2, EY6, 3.5 - DIAM_S_HW, EY6)
line(3.5 + DIAM_S_HW, EY6, 3.5 + DIAM_S_HW + 0.6, EY6)
routed([(3.5 + DIAM_S_HW + 0.6, EY6), (3.5 + DIAM_S_HW + 0.6, EY7), (EX7 - ENT_W/2, EY7)])
card(EX6 + ENT_W/2 + 0.12, EY6 - 0.22, '1')
card(EX7 - ENT_W/2 - 0.12, EY7 - 0.22, '1')

# ── R7: 工作订单 → 评价信息  1:1 "产生" — via right corridor ─────
draw_relationship(7.5, 2.6, '产生')
routed([(EX7 + ENT_W/2, EY7), (7.5, EY7), (7.5, 2.6 + DIAM_HH)])
routed([(7.5, 2.6 - DIAM_HH), (7.5, EY8), (EX8 + ENT_W/2, EY8)])
card(7.62, EY7 - ENT_H/2 + 0.12, '1')
card(7.62, EY8 + ENT_H/2 - 0.12, '1')


# ═══════════════════════════════════════════════════════════════════════════
#  Title
# ═══════════════════════════════════════════════════════════════════════════
ax.text(3.25, 12.1, '校园兼职平台E-R实体关系图',
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
print(f"A4 portrait: 8.27 x 11.69 in  (native output, no scaling needed)")

# ═══════════════════════════════════════════════════════════════════════════
#  Attribute count verification
# ═══════════════════════════════════════════════════════════════════════════
attr_counts = {
    '系统用户': 7,   # id, username, password_hash, phone, email, role_type, status
    '用户档案': 13,  # id, user_id, real_name, id_card_hash, id_card_image, gender, university, major, grade, balance, credit_score, avatar_url, verified_status
    '兼职分类': 5,   # id, name, parent_id, sort_order, status
    '兼职信息': 15,  # id, publisher_id, category_id, title, description, location, salary_type, salary_amount, start_time, end_time, recruit_num, hired_num, audit_status, audit_remark, status
    '求职申请': 8,   # id, job_id, applicant_id, resume_url, status, apply_time, reject_reason, review_remark
    '工作订单': 10,  # id, application_id, student_id, employer_id, job_id, amount, pay_status, settlement_status, start_date, end_date
    '评价信息': 7,   # id, order_id, reviewer_id, target_id, type, rating, comment
    '系统字典': 6,   # id, dict_type, dict_code, dict_label, sort_order, status
}
total = sum(attr_counts.values())
print(f"\n=== Attribute Count Summary ===")

# ═══════════════════════════════════════════════════════════════════════════
#  Overlap verification (automated)
# ═══════════════════════════════════════════════════════════════════════════
def verify_no_overlaps():
    def aw(name):
        tw = sum(0.095 if ord(c) > 127 else 0.055 for c in name)
        return max(tw + 0.22, 0.50)
    rows = [
        ['用户ID', '用户名', '密码哈希'],
        ['手机号', '邮箱', '角色类型', '状态'],
        ['分类ID', '分类名称', '父级ID'],
        ['排序', '状态'],
        ['字典ID', '字典类型', '字典编码'],
        ['字典标签', '排序', '状态'],
        ['档案ID', '用户ID', '真实姓名'],
        ['身份证哈希', '性别', '学校'],
        ['申请ID', '兼职ID', '申请者ID', '简历URL'],
        ['申请状态', '申请时间', '拒绝原因'],
        ['订单ID', '申请ID', '学生ID'],
        ['雇主ID', '岗位ID'],
        ['订单金额', '支付状态', '结算状态'],
        ['开始日期', '结束日期'],
        ['评价ID', '订单ID', '评价者ID'],
    ]
    ok = True
    for names in rows:
        for i in range(len(names)-1):
            need = aw(names[i])/2 + aw(names[i+1])/2
            if need > SP:
                print(f'  OVERLAP: {names[i]} <-> {names[i+1]} (need={need:.3f} > SP={SP})')
                ok = False
    if ok:
        print('\n=== Overlap Check: ALL CLEAR (no overlaps detected) ===')
    else:
        print('\n=== Overlap Check: FAILURES FOUND ===')
verify_no_overlaps()
for name, count in attr_counts.items():
    print(f"  {name}: {count} attributes")
print(f"  TOTAL: {total} attributes (excluding deleted/version/create_time/update_time)")
