package com.sinomaps.geobookar.utility;

public class DimenTool {
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0175 A[SYNTHETIC, Splitter:B:17:0x0175] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x020b A[SYNTHETIC, Splitter:B:31:0x020b] */
    /* JADX WARNING: Removed duplicated region for block: B:43:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void gen() {
        /*
            java.io.File r7 = new java.io.File
            java.lang.String r24 = "./app/src/main/res/values/dimens.xml"
            r0 = r24
            r7.<init>(r0)
            r10 = 0
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.StringBuilder r17 = new java.lang.StringBuilder
            r17.<init>()
            java.lang.StringBuilder r19 = new java.lang.StringBuilder
            r19.<init>()
            java.lang.StringBuilder r22 = new java.lang.StringBuilder
            r22.<init>()
            java.io.PrintStream r24 = java.lang.System.out     // Catch:{ IOException -> 0x0217 }
            java.lang.String r25 = "生成不同分辨率："
            r24.println(r25)     // Catch:{ IOException -> 0x0217 }
            java.io.BufferedReader r11 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0217 }
            java.io.FileReader r24 = new java.io.FileReader     // Catch:{ IOException -> 0x0217 }
            r0 = r24
            r0.<init>(r7)     // Catch:{ IOException -> 0x0217 }
            r0 = r24
            r11.<init>(r0)     // Catch:{ IOException -> 0x0217 }
            r8 = 1
        L_0x0039:
            java.lang.String r21 = r11.readLine()     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            if (r21 == 0) goto L_0x0179
            java.lang.String r24 = "</dimen>"
            r0 = r21
            r1 = r24
            boolean r24 = r0.contains(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            if (r24 == 0) goto L_0x0130
            r24 = 0
            r25 = 62
            r0 = r21
            r1 = r25
            int r25 = r0.indexOf(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            int r25 = r25 + 1
            r0 = r21
            r1 = r24
            r2 = r25
            java.lang.String r12 = r0.substring(r1, r2)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r24 = 60
            r0 = r21
            r1 = r24
            int r24 = r0.lastIndexOf(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            int r24 = r24 + -2
            r0 = r21
            r1 = r24
            java.lang.String r6 = r0.substring(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r24 = 62
            r0 = r21
            r1 = r24
            int r24 = r0.indexOf(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            int r24 = r24 + 1
            java.lang.String r25 = "</dimen>"
            r0 = r21
            r1 = r25
            int r25 = r0.indexOf(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            int r25 = r25 + -2
            r0 = r21
            r1 = r24
            r2 = r25
            java.lang.String r24 = r0.substring(r1, r2)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.Integer r24 = java.lang.Integer.valueOf(r24)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            int r9 = r24.intValue()     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.StringBuilder r24 = r13.append(r12)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            double r0 = (double) r9     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r26 = r0
            r28 = 4603579539098121011(0x3fe3333333333333, double:0.6)
            double r26 = r26 * r28
            long r26 = java.lang.Math.round(r26)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r26
            int r0 = (int) r0     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r25 = r0
            java.lang.StringBuilder r24 = r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r24
            java.lang.StringBuilder r24 = r0.append(r6)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "                    "
            r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.StringBuilder r24 = r15.append(r12)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            double r0 = (double) r9     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r26 = r0
            r28 = 4604930618986332160(0x3fe8000000000000, double:0.75)
            double r26 = r26 * r28
            long r26 = java.lang.Math.round(r26)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r26
            int r0 = (int) r0     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r25 = r0
            java.lang.StringBuilder r24 = r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r24
            java.lang.StringBuilder r24 = r0.append(r6)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "                    "
            r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r17
            java.lang.StringBuilder r24 = r0.append(r12)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            double r0 = (double) r9     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r26 = r0
            r28 = 4606281698874543309(0x3feccccccccccccd, double:0.9)
            double r26 = r26 * r28
            long r26 = java.lang.Math.round(r26)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r26
            int r0 = (int) r0     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r25 = r0
            java.lang.StringBuilder r24 = r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r24
            java.lang.StringBuilder r24 = r0.append(r6)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "                    "
            r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r19
            r1 = r21
            java.lang.StringBuilder r24 = r0.append(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "                    "
            r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r22
            r1 = r21
            java.lang.StringBuilder r24 = r0.append(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "                    "
            r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
        L_0x012c:
            int r8 = r8 + 1
            goto L_0x0039
        L_0x0130:
            r0 = r21
            java.lang.StringBuilder r24 = r13.append(r0)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "                    "
            r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r21
            java.lang.StringBuilder r24 = r15.append(r0)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "                    "
            r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r17
            r1 = r21
            java.lang.StringBuilder r24 = r0.append(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "                    "
            r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r19
            r1 = r21
            java.lang.StringBuilder r24 = r0.append(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "                    "
            r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r22
            r1 = r21
            java.lang.StringBuilder r24 = r0.append(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "                    "
            r24.append(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            goto L_0x012c
        L_0x016e:
            r4 = move-exception
            r10 = r11
        L_0x0170:
            r4.printStackTrace()     // Catch:{ all -> 0x0208 }
            if (r10 == 0) goto L_0x0178
            r10.close()     // Catch:{ IOException -> 0x0202 }
        L_0x0178:
            return
        L_0x0179:
            r11.close()     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.io.PrintStream r24 = java.lang.System.out     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "<!--  sw480 -->"
            r24.println(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.io.PrintStream r24 = java.lang.System.out     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r24
            r0.println(r13)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.io.PrintStream r24 = java.lang.System.out     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "<!--  sw600 -->"
            r24.println(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.io.PrintStream r24 = java.lang.System.out     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r24
            r0.println(r15)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.io.PrintStream r24 = java.lang.System.out     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "<!--  sw720 -->"
            r24.println(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.io.PrintStream r24 = java.lang.System.out     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r24
            r1 = r17
            r0.println(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.io.PrintStream r24 = java.lang.System.out     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r25 = "<!--  sw800 -->"
            r24.println(r25)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.io.PrintStream r24 = java.lang.System.out     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r24
            r1 = r19
            r0.println(r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r14 = "./app/src/main/res/values-sw480dp-land/dimens.xml"
            java.lang.String r16 = "./app/src/main/res/values-sw600dp-land/dimens.xml"
            java.lang.String r18 = "./app/src/main/res/values-sw720dp-land/dimens.xm"
            java.lang.String r20 = "./app/src/main/res/values-sw800dp-land/dimens.xml"
            java.lang.String r23 = "./app/src/main/res/values-w820dp/dimens.xml"
            java.lang.String r24 = r13.toString()     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r24
            writeFile(r14, r0)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r24 = r15.toString()     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r16
            r1 = r24
            writeFile(r0, r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r24 = r17.toString()     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r18
            r1 = r24
            writeFile(r0, r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r24 = r19.toString()     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            r0 = r20
            r1 = r24
            writeFile(r0, r1)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            java.lang.String r24 = r22.toString()     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            writeFile(r23, r24)     // Catch:{ IOException -> 0x016e, all -> 0x0214 }
            if (r11 == 0) goto L_0x021a
            r11.close()     // Catch:{ IOException -> 0x01fb }
            r10 = r11
            goto L_0x0178
        L_0x01fb:
            r5 = move-exception
            r5.printStackTrace()
            r10 = r11
            goto L_0x0178
        L_0x0202:
            r5 = move-exception
            r5.printStackTrace()
            goto L_0x0178
        L_0x0208:
            r24 = move-exception
        L_0x0209:
            if (r10 == 0) goto L_0x020e
            r10.close()     // Catch:{ IOException -> 0x020f }
        L_0x020e:
            throw r24
        L_0x020f:
            r5 = move-exception
            r5.printStackTrace()
            goto L_0x020e
        L_0x0214:
            r24 = move-exception
            r10 = r11
            goto L_0x0209
        L_0x0217:
            r4 = move-exception
            goto L_0x0170
        L_0x021a:
            r10 = r11
            goto L_0x0178
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sinomaps.geobookar.utility.DimenTool.gen():void");
    }

    public static void main(String[] args) {
        gen();
    }
}
