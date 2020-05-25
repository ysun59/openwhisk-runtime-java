set terminal pdfcairo dashed font "Gill Sans,10" linewidth 2 rounded fontscale 1.0

# Line style for axes
set style line 80 lt rgb "#808080"

# Line style for grid
set style line 81 lt 0  # dashed
set style line 81 lt rgb "#cccccc"  # grey
# set missing "?"

set grid back linestyle 81
set border 3 back linestyle 80 # Remove border on top and right.  These
             # borders are useless and make it harder
                          # to see plotted lines near the border.
                              # Also, put it in grey; no need for so much emphasis on a border.
                              set xtics nomirror
                              set ytics nomirror

set output "output/all_cost.pdf"
set ylabel "Cost reduction" font ",9" #offset 2
set xlabel "Concurrent invocations" font ",9" # offset 2.5

#unset key
#set key top right outside
#set key title "# direct links"
set key inside bottom right font ",9"
#set key above font ",7" horizontal
#set key spacing 1.5 samplen 0.5 height 0.7
unset key

set xtics font ",7" 0,1,5
set ytics font ",9" 0,0.2,2
set style line 1 lt 1 lw 0.5

#set xrange[0:16]
set xrange [0.95:5]
set yrange[0.95:2]

set format y "%Hx"
#set logscale x



#set arrow 1 from 0,0.5 to 1,0.5 nohead

#set key above width -8 vertical maxrows 2

#"data/cost.txt" using 1:(1/($2)) title "Sleep" with lines lc rgb "#4287f5" lw 3 dt 2, \

plot \
  "data/cost.txt" using 1:(1/($3)) title "Rest" with lines lc rgb "#fcbd35" lw 3 lt 2, \
  "data/cost.txt" using 1:(1/($4)) title "File hashing" with lines lc rgb "#c625cf" lw 3 lt 3, \
  "data/cost.txt" using 1:(1/($5)) title "Image class." with lines lc rgb "#00875c" lw 3 dt 3
