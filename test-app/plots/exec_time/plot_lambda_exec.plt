# Note you need gnuplot 4.4 for the pdfcairo terminal.
#set terminal pdfcairo font "Helvetica,8" linewidth 4 rounded
#set size ratio 0.6
#set terminal postscript monochrome font "Helvetica, 22" linewidth 4 rounded
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

set output "./output/exec_time.pdf"
set ylabel "Execution time (ms)" font ",9" offset 1.5
set y2label "Cost ($)" font ",9" offset -3.
set xlabel "CPU allocation" font ",9" #offset 2


unset key
#set key inside top left font ",9"
#set key title "# direct links"
#set key at 1.8, 160
#set key above font ",7" horizontal
#set key spacing 1.25 samplen 0.5 height 0.7
#unset key

set xtics font ",7" 0,0.25,2
set ytics nomirror font ",9" 0,20.,160.0
set y2tics font ",9" 0,0.000001,0.000002

# set ytics 10 nomirror tc lt 1
# set ylabel '2*x' tc lt 1

# set y2tics 20 nomirror tc lt 2
# set y2label '4*x' tc lt 2

# plot 2*x linetype 1, 4*x/2+.5 linetype 2

#set format y2 '%H'

#set style line 1 lt 1 lw 0.5
set yrange[0:160]
set y2range[0:0.000002]
#set xrange[1:2.1]
#set xrange [:8]
#set logscale y


set label 1 "Execution time" at 1.15,35 font ",9"
set label 2 "Cost" at 1.5,110 font ",9"

plot \
  "./data_raw/lambda_data.txt" using (($1)):(($2)) title "Execution time" with lines dashtype "..._." lc rgb "red" lw 1.5 lt 1, \
  "./data_raw/lambda_data.txt" using (($1)):(($1)*($2)*(0.00000002083)) title "Cost" with lines dashtype '.____.' lc rgb "orange" lw 1.5 lt 2 axes x1y2



#, \
# "./generated/error-SGD.txt" using (($1)):2 title "SGD" with lines dashtype "- -" lc rgb "#006400" lw 1.5, \
# "./generated/error-tensorflow.txt" using (($1)):2 title "Tensorflow" with lines dashtype '.____.' lc rgb "orange" lw 1.5 lt 2, \
# "./generated/error-web_server.txt" using (($1)):2 title "Web Server" with lines dashtype ".." lc rgb "blue" lw 1.5
# "./tmp/cdf_fct_fifo.txt" using (($1/1000)):2 title "Oblivious" with lines dashtype ". ." lc rgb "black" lw 1.5 lt 2
