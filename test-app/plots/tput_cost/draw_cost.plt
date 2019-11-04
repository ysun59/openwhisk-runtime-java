# Note you need gnuplot 4.4 for the pdfcairo terminal.
#set terminal pdfcairo font "Helvetica,8" linewidth 4 rounded
#set size ratio 0.6
#set terminal postscript monochrome font "Helvetica, 22" linewidth 4 rounded
set terminal pdfcairo dashed font "Gill Sans,10" linewidth 2 rounded fontscale 1.0

# Line style for axes
set style line 80 lt rgb "#808080"

# Line style for grid
set style line 81 lt 0  # dashed
set style line 81 lt rgb "#808080"  # grey
# set missing "?"

set grid back linestyle 81
set border 3 back linestyle 80 # Remove border on top and right.  These
             # borders are useless and make it harder
                          # to see plotted lines near the border.
                              # Also, put it in grey; no need for so much emphasis on a border.
                              set xtics nomirror
                              set ytics nomirror

set output "output/container_cost.pdf"

set xlabel "Functions co-executed" font ",9" # offset 2.5
set ylabel "Throughput (invocations/s)" font ",9" offset 1.5
set y2label "Cost ($/100M)" font ",9" offset -3.


#unset key
#set key title "# direct links"
set key inside bottom right font ",9"
#set key at 3400, 12
#set key above font ",7" horizontal
#set key spacing 1.25 samplen 0.5 height 0.7
#unset key

set xtics font ",9"
set ytics font ",9" 0,5.,25.0
set y2tics font ",9" 0,5.,20

set style line 1 lt 1 lw 0.5
set yrange[0:25]
set y2range[0:20]
#set xrange[1:2.1]
#set xrange [:8]
#set logscale y
plot \
  "./data/data.txt" using 1:2 title "Throughput" with lines dashtype "..._." lc rgb "red" lw 3, \
  "./data/data.txt" using 1:4 title "Cost" with lines dashtype '.____.' lc rgb "orange" lw 3 lt 2 axes x1y2
